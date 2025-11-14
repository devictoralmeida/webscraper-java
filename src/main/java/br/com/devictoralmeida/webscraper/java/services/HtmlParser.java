package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import org.springframework.transaction.annotation.Transactional;

public interface HtmlParser {
    @Transactional
    ParsedNewsDTO parseNewsDetails(String html, PartialNewsDTO partialNews);
}
