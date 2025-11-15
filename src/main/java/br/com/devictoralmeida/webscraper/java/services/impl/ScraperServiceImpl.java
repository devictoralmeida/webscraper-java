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

        // 1. Obter lista de DTOs parciais (já filtrados)
        List<PartialNewsDTO> newsToProcess = getNewsToProcess(pageLimit);
        if (newsToProcess.isEmpty()) {
            log.info("Nenhuma notícia nova para processar.");
            return new ArrayList<>();
        }

        // 2. Buscar e parsear em paralelo (HTTP I/O)
        List<ParsedNewsDTO> parsedNewsList = fetchAndParseNewsInParallel(newsToProcess);

        // 3. Resolver autores em lote (DB I/O)
        Map<String, Author> authorMap = getOrCreateAuthorsInBatch(parsedNewsList);

        // 4. Salvar no banco (DB I/O)
        List<NewsResponseDTO> savedNews = buildAndSaveNewsEntities(parsedNewsList, authorMap);

        log.info("Processo de scraping concluído. {} notícias salvas.", savedNews.size());
        return savedNews;
    }

    /**
     * Passo 1: Busca a lista de notícias da fonte (HTML + API) e filtra as que já existem no banco.
     */
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

    /**
     * Passo 2: Recebe DTOs parciais e dispara o download + parse de todas em Threads Virtuais.
     */
    private List<ParsedNewsDTO> fetchAndParseNewsInParallel(List<PartialNewsDTO> newsToProcess) {
        log.info("{} notícias novas para processar com Threads Virtuais...", newsToProcess.size());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Submete todas as tarefas
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

            // *** OTIMIZAÇÃO DE LOOP ***
            // Trocamos o 'for (var future : futures)' por um Stream API.
            return futures.stream()
                    .map(this::getFutureResult) // Mapeia cada Future para seu resultado
                    .filter(Objects::nonNull)   // Filtra os que falharam (retornaram null)
                    .toList();                  // Coleta em uma lista

        } catch (Exception e) {
            log.error("Erro durante a execução paralela com Threads Virtuais", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>(); // Retorna lista vazia em caso de falha no pool
        }
    }

    /**
     * Método auxiliar para extrair o resultado de um Future, tratando exceções.
     */
    private <T> T getFutureResult(Future<T> future) {
        try {
            return future.get(); // Bloqueia esta thread (virtual) esperando o resultado
        } catch (Exception e) {
            log.error("Falha ao obter resultado de uma tarefa futura (Future): {}", e.getMessage());
            Thread.currentThread().interrupt(); // Restaura o status de interrupção
            return null;
        }
    }

    /**
     * Passo 3: Recebe os dados parseados, agrupa os nomes dos autores e os busca/cria em lote no banco.
     */
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

        List<Author> newAuthorsToSave = authorNames.stream()
                .filter(name -> !authorMap.containsKey(name))
                .map(Author::new)
                .toList();

        if (!newAuthorsToSave.isEmpty()) {
            log.info("Criando {} novos autores...", newAuthorsToSave.size());
            List<Author> savedNewAuthors = this.authorRepository.saveAll(newAuthorsToSave);
            savedNewAuthors.forEach(author -> authorMap.put(author.getName(), author));
        }

        return authorMap;
    }

    /**
     * Passo 4: Monta as entidades 'News' finais com os Autores e salva tudo em lote.
     */
    private List<NewsResponseDTO> buildAndSaveNewsEntities(List<ParsedNewsDTO> parsedNewsList, Map<String, Author> authorMap) {
        List<News> entitiesToSave = parsedNewsList.stream()
                .filter(parsed -> parsed.getPublishDate() != null && parsed.getAuthorName() != null)
                .map(parsedDto -> new News(
                        parsedDto.getPartialNews(),
                        parsedDto.getSubtitle(),
                        parsedDto.getContent(),
                        parsedDto.getPublishDate(),
                        authorMap.get(parsedDto.getAuthorName())
                ))
                .toList();

        return this.repository.saveAll(entitiesToSave).stream()
                .map(NewsResponseDTO::new)
                .toList();
    }
}