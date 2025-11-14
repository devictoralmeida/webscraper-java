package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;

public interface HtmlParser {
    ParsedNewsDTO parseNewsDetails(String html, PartialNewsDTO partialNews);
}
