package br.com.devictoralmeida.webscraper.java.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_news", schema = "public")
@NoArgsConstructor
public class News implements Serializable {
    @Serial
    private static final long serialVersionUID = 1084934057135367842L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_news_id_seq")
    @SequenceGenerator(
            name = "tb_news_id_seq",
            allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "url", columnDefinition = "TEXT", unique = true, nullable = false)
    private String url;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "author")
    private String author;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "publish_at", nullable = false)
    private LocalDateTime publishDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public News(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public News(String url, String title, String subtitle, String author, String content, LocalDateTime publishDate) {
        this.url = url;
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.content = content;
        this.publishDate = publishDate;
    }
}
