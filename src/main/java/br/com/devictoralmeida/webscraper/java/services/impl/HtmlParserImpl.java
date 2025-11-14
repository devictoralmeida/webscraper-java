package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlParserImpl implements HtmlParser {
    private final AuthorRepository authorRepository;

    @Value("${user.agent}")
    private String userAgent;

    @Override
    public News parseNewsDetails(PartialNewsDTO partialNews) {
        try {
            Document doc = Jsoup.connect(partialNews.getUrl())
                    .userAgent(this.userAgent)
                    .get();

            String subtitle = Optional.ofNullable(doc.selectFirst("div[data-ds-component='article-title'] > div"))
                    .map(Element::text)
                    .orElse(null);
            Author author = getAuthor(doc.selectFirst("div[data-ds-component='author-bio'] a"));
            String content = getContent(doc);
            LocalDateTime date = getPublishedDate(doc.selectFirst("time[datetime]"));

            return new News(
                    partialNews,
                    subtitle,
                    content,
                    date,
                    author
            );
        } catch (IOException e) {
            throw new RuntimeException("Falha ao fazer o parse do Jsoup na URL: " + partialNews.getUrl(), e);
        }
    }

    private String getContent(Document doc) {
        return Optional.ofNullable(doc.selectFirst("article[data-ds-component='article']"))
                .map(article -> {
                    article.select("div[data-ds-component='ad'], div.cta-middle, iframe, div[data-component-type='ads']").remove();
                    return article.text();
                })
                .orElse("");
    }

    private LocalDateTime getPublishedDate(Element timeElement) {
        return Optional.ofNullable(timeElement)
                .map(el -> el.attr("datetime"))
                .filter(dateStr -> !ObjectUtils.isEmpty(dateStr))
                .map(DateUtils::parse)
                .orElse(null);
    }

    private Author getAuthor(Element authorElement) {
        return Optional.ofNullable(authorElement)
                .map(Element::text)
                .map(authorName -> this.authorRepository.findByName(authorName)
                        .orElseGet(() -> this.authorRepository.save(new Author(authorName))))
                .orElse(null);
    }
}
