package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlParserImpl implements HtmlParser {
    @Value("${user.agent}")
    private String userAgent;

    @Override
    public News parseNewsDetails(String url, News partialNews) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(this.userAgent)
                    .get();


            // Abordagem segura para pegar o subtítulo
            Element subtitleElement = doc.selectFirst("div[data-ds-component='article-title'] > div");
            String subtitle = (subtitleElement != null) ? subtitleElement.text() : null; // Use null se não encontrar

            // Abordagem segura para o autor
            Element authorElement = doc.selectFirst("div[data-ds-component='author-bio'] a");
            String author = (authorElement != null) ? authorElement.text() : null; // Use null se não encontrar

            String content = "";
            Element articleElement = doc.selectFirst("article[data-ds-component='article']");

            if (articleElement != null) {
                articleElement.select("div[data-ds-component='ad']").remove();
                articleElement.select("div.cta-middle").remove();
                articleElement.select("iframe").remove();
                articleElement.select("div[data-component-type='ads']").remove();
                content = articleElement.text();
            }

            Element timeElement = doc.selectFirst("time[datetime]");
            String dateStr = timeElement != null ? timeElement.attr("datetime") : null;
            LocalDateTime date = null;

            if (dateStr != null && !dateStr.isEmpty()) {
                date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            }

            return new News(
                    partialNews.getUrl(),
                    partialNews.getTitle(),
                    subtitle,
                    author,
                    content,
                    date
            );
        } catch (IOException e) {
            throw new RuntimeException("Falha ao fazer o parse do Jsoup na URL: " + url, e);
        }
    }
}
