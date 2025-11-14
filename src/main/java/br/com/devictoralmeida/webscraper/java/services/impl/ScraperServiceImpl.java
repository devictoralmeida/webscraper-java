package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {
    private final NewsListProvider listProvider;
    private final HtmlParser parser;
    private final NewsRepository repository;

    @Value("${scraper.delay.ms}")
    private int delay;

    @Override
    public List<NewsResponseDTO> execute(int pageLimit) {
        log.info("Iniciando processo de scraping...");

        List<PartialNewsDTO> newsList = this.listProvider.fetchNewsList(pageLimit);
        log.info("{} notícias encontradas para processamento.", newsList.size());

        // 2. Filtra URLs que já existem (Evita N+1 queries)
        List<String> candidateUrls = newsList.stream().map(PartialNewsDTO::getUrl).toList();
        Set<String> existingUrls = new HashSet<>(this.repository.findUrlsIn(candidateUrls));
        log.info("{} notícias já existem no banco e serão puladas.", existingUrls.size());

        List<PartialNewsDTO> newNewsToProcess = newsList.stream()
                .filter(partial -> !existingUrls.contains(partial.getUrl()))
                .toList();

        // 3. Processa apenas as notícias novas
        List<News> entitiesToSave = new ArrayList<>();
        log.info("{} notícias novas para processar.", newNewsToProcess.size());

        newNewsToProcess.forEach(partialNewsDto -> {
            try {
                log.info("Processando notícia: {}", partialNewsDto.getTitle());
                News completeNews = this.parser.parseNewsDetails(partialNewsDto); // Passa o DTO

                // Validação final antes de adicionar
                if (completeNews.getPublishDate() != null && completeNews.getAuthor() != null) {
                    entitiesToSave.add(completeNews);
                }

                Thread.sleep(this.delay);
            } catch (Exception e) {
                log.error("Falha ao processar notícia: {}", partialNewsDto.getUrl(), e);
            }
        });
        log.info("Processo de scraping concluído. Salvando {} novas notícias...", entitiesToSave.size());
        return this.repository.saveAll(entitiesToSave).stream().map(NewsResponseDTO::new).toList();
    }
}
