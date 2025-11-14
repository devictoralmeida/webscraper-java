package br.com.devictoralmeida.webscraper.java.repositories;

import br.com.devictoralmeida.webscraper.java.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n.url FROM News n WHERE n.url IN :urls")
    List<String> findUrlsIn(List<String> urls);

    @Query("""
                 SELECT n FROM News n WHERE n.author.id = :authorId
                 AND n.publishDate >= :startOfDay
                 AND n.publishDate <= :endOfDay
                 ORDER BY n.publishDate DESC
            """)
    List<News> findNewsByAuthorAndDateRange(
            @Param("authorId") Long authorId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
