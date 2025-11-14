package br.com.devictoralmeida.webscraper.java.services;

import org.springframework.transaction.annotation.Transactional;

public interface HtmlFetcher {
    @Transactional
    String fetchHtmlContent(String url);
}
