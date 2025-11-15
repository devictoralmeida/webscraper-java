package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {
    private final NewsListProvider listProvider;
    private final HtmlParser parser;
    private final NewsRepository repository;
    private final AuthorRepository authorRepository;
    private final HttpClient httpClient;

    @Override
    @Transactional
    public List<NewsResponseDTO> execute(int pageLimit) {
        log.info("Iniciando processo de scraping...");

        List<PartialNewsDTO> newsToProcess = getNewsToProcess(pageLimit);

        if (newsToProcess.isEmpty()) {
            log.info("Nenhuma notícia nova para processar.");
            return new ArrayList<>();
        }

        List<ParsedNewsDTO> parsedNewsList = fetchAndParseNewsInParallel(newsToProcess);
        Map<String, Author> authorMap = getOrCreateAuthorsInBatch(parsedNewsList);
        List<NewsResponseDTO> savedNews = buildAndSaveNewsEntities(parsedNewsList, authorMap);

        log.info("Processo de scraping concluído. {} notícias salvas.", savedNews.size());
        return savedNews;
    }

    private List<PartialNewsDTO> getNewsToProcess(int pageLimit) {
        List<PartialNewsDTO> newsList = this.listProvider.fetchNewsList(pageLimit);
        log.info("{} notícias encontradas para processamento.", newsList.size());

        List<String> candidateUrls = newsList.stream().map(PartialNewsDTO::getUrl).toList();
        Set<String> existingUrls = new HashSet<>(this.repository.findUrlsIn(candidateUrls));
        log.info("{} notícias já existem no banco e serão puladas.", existingUrls.size());

        return newsList.stream()
                .filter(partial -> !existingUrls.contains(partial.getUrl()))
                .toList();
    }

    private List<ParsedNewsDTO> fetchAndParseNewsInParallel(List<PartialNewsDTO> newsToProcess) {
        log.info("{} notícias novas que serão processada e parseadas em paralelo com Threads Virtuais...", newsToProcess.size());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ParsedNewsDTO>> futures = newsToProcess.stream()
                    .map(partialNewsDto -> executor.submit(() -> {
                        try {
                            log.debug("Processando: {}", partialNewsDto.getTitle());
                            String html = this.httpClient.makeGetRequest(partialNewsDto.getUrl(), String.class, null, null);
                            return this.parser.parseNewsDetails(html, partialNewsDto);
                        } catch (Exception e) {
                            log.error("Falha ao processar notícia: {}", partialNewsDto.getUrl(), e);
                            return null;
                        }
                    }))
                    .toList();

            return futures.stream()
                    .map(this::getFutureResult)
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            log.error("Erro durante a execução paralela com Threads Virtuais", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }

    private <T> T getFutureResult(Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.error("Falha ao obter resultado de uma tarefa futura: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private Map<String, Author> getOrCreateAuthorsInBatch(List<ParsedNewsDTO> parsedNewsList) {
        Set<String> authorNames = parsedNewsList.stream()
                .map(ParsedNewsDTO::getAuthorName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (authorNames.isEmpty()) {
            return Map.of();
        }

        Map<String, Author> authorMap = this.authorRepository.findByNameIn(authorNames).stream()
                .collect(Collectors.toMap(Author::getName, Function.identity()));

        List<Author> newAuthors = authorNames.stream()
                .filter(name -> !authorMap.containsKey(name))
                .map(Author::new)
                .toList();

        if (!newAuthors.isEmpty()) {
            log.info("Criando {} novos autores...", newAuthors.size());
            newAuthors.forEach(author -> authorMap.put(author.getName(), author));
        }

        return authorMap;
    }

    private List<NewsResponseDTO> buildAndSaveNewsEntities(List<ParsedNewsDTO> parsedNewsList, Map<String, Author> authorMap) {
        List<News> entitiesToSave = parsedNewsList.stream()
                .filter(this::hasPublishDateAndAuthor)
                .map(parsedDto -> new News(parsedDto, authorMap.get(parsedDto.getAuthorName())
                ))
                .toList();

        return this.repository.saveAll(entitiesToSave).stream()
                .map(NewsResponseDTO::new)
                .toList();
    }

    private boolean hasPublishDateAndAuthor(ParsedNewsDTO parsedDto) {
        return Objects.nonNull(parsedDto.getPublishDate()) && Objects.nonNull(parsedDto.getAuthorName());
    }
}