package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsService {
    @Transactional
    List<NewsResponseDTO> scrapeAndSaveNews(int pageLimit);

    @Transactional(readOnly = true)
    List<AuthorNewsCountResponseDTO> findTopAuthorsByDateRange(DateRangeRequestDTO dto);

    @Transactional(readOnly = true)
    List<NewsResponseDTO> findNewsByAuthorAndDateRange(Long authorId, DateRangeRequestDTO dto);
}
