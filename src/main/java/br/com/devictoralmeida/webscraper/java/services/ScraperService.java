package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScraperService {
    @Transactional
    List<NewsResponseDTO> execute(int pageLimit);
}
