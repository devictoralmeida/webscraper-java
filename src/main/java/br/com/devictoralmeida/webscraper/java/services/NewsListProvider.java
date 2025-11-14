package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;

import java.util.List;

public interface NewsListProvider {
    List<PartialNewsDTO> fetchNewsList(int pageLimit);
}
