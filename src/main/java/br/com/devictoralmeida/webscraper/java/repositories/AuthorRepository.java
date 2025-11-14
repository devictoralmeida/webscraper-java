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
                    SELECT NEW br.com.devictoralmeida.webscraper.java.dtos.AuthorNewsCountResponseDTO(
                        n.author.id,
                        n.author.name,
                        COUNT(n.id)
                    )
                    FROM News n
                    WHERE n.publishDate >= :startOfDay AND n.publishDate <= :endOfDay
                    GROUP BY n.author.id, n.author.name
                    ORDER BY COUNT(n.id) DESC
            """)
    List<AuthorNewsCountResponseDTO> findAuthorsWithMostPublicationsOnDateRange(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT a FROM Author a WHERE a.name IN :names")
    List<Author> findByNameIn(@Param("names") Set<String> names);
}
