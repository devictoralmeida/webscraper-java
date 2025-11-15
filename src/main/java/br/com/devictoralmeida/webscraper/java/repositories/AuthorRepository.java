package br.com.devictoralmeida.webscraper.java.repositories;

import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("""
            SELECT new br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO(
                a.id, a.name, COUNT(n.id)
            )
            FROM Author a
            JOIN a.newsList n
            WHERE n.publishDate >= :startDate
            AND n.publishDate <= :endDate
            GROUP BY a.id, a.name
            ORDER BY COUNT(n.id) DESC
            """)
    List<AuthorNewsCountResponseDTO> findAuthorsWithMostPublicationsOnDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a FROM Author a WHERE a.name IN :names")
    List<Author> findByNameIn(@Param("names") Set<String> names);
}
