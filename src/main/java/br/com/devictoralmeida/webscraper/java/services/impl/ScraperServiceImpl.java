package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.NewsResponseDTO;
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
import java.util.List;

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

        List<News> newsList = this.listProvider.fetchNewsList(pageLimit);

        log.info("{} notícias encontradas para processamento.", newsList.size());

        List<News> entitiesToSave = new ArrayList<>();

        newsList.forEach(news -> {
            try {
                log.info("Processando notícia: {}", news.getTitle());

                // 2. Baixa o HTML da notícia individual
//                String rawHtml = this.fetcher.fetchHtmlContent(news.getUrl());

                // 3. Faz o parse dos detalhes (Autor, Data, Conteúdo)
                News completeNews = this.parser.parseNewsDetails(news.getUrl(), news);
                entitiesToSave.add(completeNews);

//                savedEntities.add(repository.save(completeNews));

                // Pequeno delay para ser gentil com o servidor (opcional)
                Thread.sleep(this.delay);
            } catch (Exception e) {
                log.error("Falha ao processar notícia: {}", news.getUrl(), e);
            }
        });
        log.info("Processo de scraping concluído. {} notícias salvas.", entitiesToSave.size());
        return this.repository.saveAll(entitiesToSave).stream().map(NewsResponseDTO::new).toList();
    }
}
