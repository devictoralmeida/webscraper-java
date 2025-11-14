package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.NewsService;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final ScraperService scrapingService;
    private final AuthorRepository authorRepository;
    private final NewsRepository newsRepository;

    @Override
    public List<NewsResponseDTO> scrapeAndSaveNews(int pageLimit) {
        return this.scrapingService.execute(pageLimit);
    }

    @Override
    public List<AuthorNewsCountResponseDTO> findTopAuthorsByDateRange(DateRangeRequestDTO dto) {
        return this.authorRepository.findAuthorsWithMostPublicationsOnDateRange(
                dto.getInicio().with(LocalTime.MIN),
                dto.getFim().with(LocalTime.MAX)
        );
    }

    @Override
    public List<NewsResponseDTO> findNewsByAuthorAndDateRange(Long authorId, DateRangeRequestDTO dto) {
        return this.newsRepository.findNewsByAuthorAndDateRange(
                        authorId,
                        dto.getInicio().with(LocalTime.MIN),
                        dto.getFim().with(LocalTime.MAX)
                ).stream()
                .map(NewsResponseDTO::new)
                .toList();
    }
}
