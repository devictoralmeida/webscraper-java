package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.exception.ParametrosDeConsultaInvalidosException;
import br.com.devictoralmeida.webscraper.java.exception.RecursoNaoEncontradoException;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.NewsService;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final ScraperService scrapingService;
    private final AuthorRepository authorRepository;
    private final NewsRepository newsRepository;

    @Override
    @Transactional
    public List<NewsResponseDTO> scrapeAndSaveNews(int pageLimit) {
        return this.scrapingService.execute(pageLimit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorNewsCountResponseDTO> findTopAuthorsByDateRange(DateRangeRequestDTO dto) {
        validateDateRange(dto);
        return this.authorRepository.findAuthorsWithMostPublicationsOnDateRange(
                dto.getInicio().with(LocalTime.MIN),
                dto.getFim().with(LocalTime.MAX)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsResponseDTO> findNewsByAuthorAndDateRange(Long authorId, DateRangeRequestDTO dto) {
        existsAuthorById(authorId);
        validateDateRange(dto);

        return this.newsRepository.findNewsByAuthorAndDateRange(
                        authorId,
                        dto.getInicio().with(LocalTime.MIN),
                        dto.getFim().with(LocalTime.MAX)
                ).stream()
                .map(NewsResponseDTO::new)
                .toList();
    }

    private void existsAuthorById(Long authorId) {
        if (!this.authorRepository.existsById(authorId)) {
            throw new RecursoNaoEncontradoException(Constants.AUTOR_NAO_ENCONTRADO);
        }
    }

    private void validateDateRange(DateRangeRequestDTO dto) {
        if (dto.getInicio().isAfter(dto.getFim())) {
            throw new ParametrosDeConsultaInvalidosException(Constants.INTERVALO_DATA_INVALIDO);
        }
    }
}
