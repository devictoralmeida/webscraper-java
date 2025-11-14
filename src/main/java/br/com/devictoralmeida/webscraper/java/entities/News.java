package br.com.devictoralmeida.webscraper.java.entities;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
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

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @Column(name = "publish_at", nullable = false)
    private LocalDateTime publishDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public News(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public News(String url, String title, String subtitle, String content, LocalDateTime publishDate, Author author) {
        this.url = url;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.publishDate = publishDate;
        this.author = author;
    }

    public News(PartialNewsDTO dto, String subtitle, String content, LocalDateTime publishDate, Author author) {
        this.url = dto.getUrl();
        this.title = dto.getTitle();
        this.subtitle = subtitle;
        this.content = content;
        this.publishDate = publishDate;
        this.author = author;
    }
}
