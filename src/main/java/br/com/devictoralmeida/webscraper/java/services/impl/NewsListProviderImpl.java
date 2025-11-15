package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.exception.NegocioException;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsListProviderImpl implements NewsListProvider {
    private final HttpClient httpClient;
    private final HtmlParser parser;
    private final ObjectMapper objectMapper;

    @Value("${api.url.mercados}")
    private String apiUrlMercados;

    @Value("${post.id.mercados}")
    private String postIdMercados;

    @Value("${api.baseurl}")
    private String baseUrl;


    @Override
    public List<PartialNewsDTO> fetchNewsList(int pageLimit) {
        List<PartialNewsDTO> initialNews = this.fetchInitialNewsFromHtml();

        if (pageLimit <= initialNews.size()) {
            log.info("Limite de {} atingido apenas com notícias iniciais.", pageLimit);
            return initialNews.stream().limit(pageLimit).toList();
        }

        log.info("Buscando notícias adicionais na API para atingir o limite de {}", pageLimit);
        List<PartialNewsDTO> apiNews = this.fetchApiNews();

        Set<PartialNewsDTO> uniqueNews = new HashSet<>(initialNews);
        uniqueNews.addAll(apiNews);

        List<PartialNewsDTO> finalList = uniqueNews.stream().limit(pageLimit).toList();
        log.info("Encontradas {} notícias únicas no total para processar.", finalList.size());
        return finalList;
    }

    private List<PartialNewsDTO> fetchInitialNewsFromHtml() {
        String initialPageUrl = this.baseUrl + Constants.MERCADOS_PATH;
        log.info("Buscando lista de notícias iniciais do HTML: {}", initialPageUrl);

        try {
            String htmlString = this.httpClient.makeGetRequest(initialPageUrl, String.class, null, null);
            Document doc = this.parser.parseHtmlContent(htmlString);

            String selector = """
                            div[data-ds-component='card-xl'] h2 a,
                            div[data-ds-component='card-sm'] h2 a,
                            div.related-link a
                    """;

            List<PartialNewsDTO> result = doc.select(selector).stream()
                    .filter(entry -> !ObjectUtils.isEmpty(entry.attr("href")) && !ObjectUtils.isEmpty(entry.text()))
                    .map(link -> new PartialNewsDTO(
                            sanitizeUrl(link.attr("href").trim()),
                            link.text().trim()
                    ))
                    .collect(Collectors.toList());

            log.info("Encontradas {} notícias iniciais no HTML.", result.size());
            return result;
        } catch (Exception e) {
            log.error("Falha ao processar HTML da página inicial: {}", initialPageUrl, e);
            throw new NegocioException("Falha ao processar HTML da página inicial: " + e.getMessage());
        }
    }

    private List<PartialNewsDTO> fetchApiNews() {
        Map<String, Object> requestBody = getRequestBody();
        log.info("Buscando lista de notícias da API via POST...");

        try {
            String jsonResponse = this.httpClient.makePostRequest(this.apiUrlMercados, requestBody, String.class, null, null);
            JsonNode root = this.objectMapper.readTree(jsonResponse);

            return StreamSupport.stream(root.spliterator(), false)
                    .map(node -> Map.entry(
                            sanitizeUrl(node.path("post_permalink").asText("")),
                            node.path("post_title").asText("")
                    ))
                    .filter(entry -> !ObjectUtils.isEmpty(entry.getKey()) && !ObjectUtils.isEmpty(entry.getValue()))
                    .map(entry -> new PartialNewsDTO(entry.getKey(), entry.getValue()))
                    .toList();
        } catch (Exception e) {
            log.error("Erro durante o parse do JSON da API", e);
            throw new NegocioException("Erro durante o parse do JSON da API");
        }
    }

    private Map<String, Object> getRequestBody() {
        return Map.of(
                "post_id", this.postIdMercados,
                "categories", List.of(Constants.UM),
                "tags", List.of()
        );
    }

    private String sanitizeUrl(String url) {
        return url.contains("#") ? url.substring(0, url.indexOf('#')) : url;
    }
}