package br.com.devictoralmeida.webscraper.java.repositories;

import br.com.devictoralmeida.webscraper.java.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
