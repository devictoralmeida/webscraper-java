package br.com.devictoralmeida.webscraper.java.repositories;

import br.com.devictoralmeida.webscraper.java.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
