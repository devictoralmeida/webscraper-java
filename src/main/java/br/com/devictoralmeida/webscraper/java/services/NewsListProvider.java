package br.com.devictoralmeida.webscraper.java.services;

import br.com.devictoralmeida.webscraper.java.entities.News;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsListProvider {
    @Transactional
    List<News> fetchNewsList(int pageLimit);
}
