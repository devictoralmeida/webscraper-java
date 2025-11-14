package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.entities.News;
import org.springframework.transaction.annotation.Transactional;

public interface HtmlParser {
    @Transactional
    News parseNewsDetails(String url, News partialNews);
}
