package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;

import java.util.List;

public interface NewsService {
    List<NewsResponseDTO> scrapeAndSaveNews(int pageLimit);

    List<AuthorNewsCountResponseDTO> findTopAuthorsByDateRange(DateRangeRequestDTO dto);

    List<NewsResponseDTO> findNewsByAuthorAndDateRange(Long authorId, DateRangeRequestDTO dto);
}
