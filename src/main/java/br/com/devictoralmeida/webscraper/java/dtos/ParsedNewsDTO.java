package br.com.devictoralmeida.webscraper.java.dtos;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class ParsedNewsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 3350497156701919262L;

    private final PartialNewsDTO partialNews;
    private final String subtitle;
    private final String content;
    private final LocalDateTime publishDate;
    private final String authorName;

    public ParsedNewsDTO(PartialNewsDTO partialNews, String subtitle, String content, LocalDateTime publishDate, String authorName) {
        this.partialNews = partialNews;
        this.subtitle = subtitle;
        this.content = content;
        this.publishDate = publishDate;
        this.authorName = authorName;
    }
}
