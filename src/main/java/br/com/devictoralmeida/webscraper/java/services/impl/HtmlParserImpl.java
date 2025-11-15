package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.exception.NegocioException;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlParserImpl implements HtmlParser {
    @Override
    public ParsedNewsDTO parseNewsDetails(String html, PartialNewsDTO partialNews) { // Assinatura mudou
        try {
            Document doc = parseHtmlContent(html);

            String subtitle = Optional.ofNullable(doc.selectFirst("div[data-ds-component='article-title'] > div"))
                    .map(Element::text)
                    .orElse(null);

            String authorName = Optional.ofNullable(doc.selectFirst("div[data-ds-component='author-bio'] a"))
                    .map(Element::text)
                    .orElse(null);

            String content = getContent(doc);
            LocalDateTime date = getPublishedDate(doc.selectFirst("time[datetime]"));

            return new ParsedNewsDTO(
                    partialNews,
                    subtitle,
                    content,
                    date,
                    authorName
            );
        } catch (Exception e) {
            log.error("Falha ao parsear HTML da URL: {}", partialNews.getUrl(), e);
            throw new NegocioException("Falha ao parsear Jsoup da URL: " + partialNews.getUrl());
        }
    }

    @Override
    public Document parseHtmlContent(String html) {
        return Jsoup.parse(html);
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
}