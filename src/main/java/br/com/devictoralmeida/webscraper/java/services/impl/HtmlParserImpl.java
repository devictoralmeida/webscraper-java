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
import java.util.Objects;

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
            String title = doc.select("div[data-ds-component='article-title'] h1").text();
            String subtitle = Objects.requireNonNull(doc.select("div[data-ds-component='article-title'] > div").first()).text();
            String author = doc.select("div[data-ds-component='author-bio'] a").text();


            String content = "";
            Element articleElement = doc.selectFirst("article[data-ds-component='article']");

            if (articleElement != null) {
                // Remove anúncios, CTAs, iframes e outros elementos indesejados
                articleElement.select("div[data-ds-component='ad']").remove();
                articleElement.select("div.cta-middle").remove();
                articleElement.select("iframe").remove();
                articleElement.select("div[data-component-type='ads']").remove();

                // Extrai o texto limpo
                content = articleElement.text();
            }

//            Exemplo de parsing, ajuste o pattern conforme o site retornar
//            LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);

//            Seleciona o primeiro elemento <time >
            Element timeElement = doc.selectFirst("time[datetime]");

            String dateStr = timeElement != null ? timeElement.attr("datetime") : null;

            LocalDateTime date = null;

            if (dateStr != null && !dateStr.isEmpty()) {
                // Parse do formato ISO 8601 com timezone
                date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                // Se quiser formatar para exibição (opcional)
//            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                // Resultado: "13/11/2025 18:26"
            }


            // Retorna o objeto imutável completo (Record)
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
