package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;

import java.util.List;

public interface ScraperService {
    List<NewsResponseDTO> execute(int pageLimit);
}
