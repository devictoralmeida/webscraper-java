package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsListProvider {
    @Transactional
    List<PartialNewsDTO> fetchNewsList(int pageLimit);
}
