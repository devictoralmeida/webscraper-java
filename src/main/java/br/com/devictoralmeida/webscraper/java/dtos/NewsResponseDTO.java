package br.com.devictoralmeida.webscraper.java.dtos;

import br.com.devictoralmeida.webscraper.java.entities.News;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Getter
public final class NewsResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2670551977462063258L;

    private final Long id;

    private final String url;

    private final String title;

    private final String subtitle;
    
    private final String author;

    private final String content;

    private final String publishDate;

    private final String createdAt;

    public NewsResponseDTO(News news) {
        this.id = news.getId();
        this.url = news.getUrl();
        this.title = news.getTitle();
        this.subtitle = news.getSubtitle();
        this.author = news.getAuthor();
        this.content = news.getContent();
        this.publishDate = news.getPublishDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.createdAt = news.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
