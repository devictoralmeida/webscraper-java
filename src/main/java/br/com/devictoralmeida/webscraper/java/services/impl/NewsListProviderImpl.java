package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsListProviderImpl implements NewsListProvider {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Valores do seu application.properties
    @Value("${api.url.mercados}")
    private String apiUrlMercados;

    @Value("${post.id.mercados}")
    private String postIdMercados;

    @Value("${api.baseurl}")
    private String baseUrl;

    @Value("${user.agent}")
    private String userAgent;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<News> fetchNewsList(int pageLimit) {
        // 1. Busca as notícias iniciais "queimadas" no HTML
        List<News> initialNews = this.fetchInitialNewsFromHtml();

        // 2. Verifica se o limite já foi atingido
        if (pageLimit <= initialNews.size()) {
            log.info("Limite de {} atingido apenas com notícias iniciais.", pageLimit);
            return initialNews.stream().limit(pageLimit).toList();
        }

        // 3. Se precisar de mais, busca na API
        log.info("Buscando notícias adicionais na API para atingir o limite de {}", pageLimit);
        List<News> apiNews = this.fetchApiNews();

        // 4. Combina, remove duplicatas (mantendo a ordem) e aplica o limite final
        // Usamos LinkedHashMap para manter a ordem e garantir URLs únicas
        Map<String, News> uniqueNewsMap = new LinkedHashMap<>();

        // Adiciona as iniciais primeiro
        initialNews.forEach(news -> uniqueNewsMap.putIfAbsent(news.getUrl(), news));
        // Adiciona as da API (só as que não estiverem no mapa)
        apiNews.forEach(news -> uniqueNewsMap.putIfAbsent(news.getUrl(), news));

        List<News> finalList = uniqueNewsMap.values().stream().limit(pageLimit).toList();

        log.info("Encontradas {} notícias únicas no total para processar.", finalList.size());
        return finalList;
    }

    /**
     * Raspa as notícias da página inicial (as ~14 que você identificou)
     *
     * @return Lista de Notícias parciais (URL e Título)
     */
    private List<News> fetchInitialNewsFromHtml() {
        List<News> result = new ArrayList<>();
        String initialPageUrl = this.baseUrl + "/mercados/"; // Monta a URL principal
        log.info("Buscando lista de notícias iniciais do HTML: {}", initialPageUrl);

        try {
            Document doc = Jsoup.connect(initialPageUrl)
                    .userAgent(this.userAgent)
                    .timeout(10000) // 10 segundos de timeout
                    .get();

            // SELETOR CORRIGIDO: Removemos a busca por 'livenews'
            String selector = "div[data-ds-component='card-xl'] h2 a, " +
                    "div[data-ds-component='card-sm'] h2 a, " +
                    "div.related-link a";

            for (Element link : doc.select(selector)) {
                String url = link.attr("href");
                String title = link.text();

                // Ignora links que não são de notícias (ex: "Mercados")
                if (title.equalsIgnoreCase("Mercados")) {
                    continue;
                }

                // Garante que a URL é absoluta (caso venha /mercados/...)
                // E trata links do "livenews" que podem ter # no meio
                if (url.contains("#")) {
                    url = url.substring(0, url.indexOf('#'));
                }

                if (!url.startsWith("http")) {
                    url = this.baseUrl + url;
                }

                result.add(new News(url, title));
            }
            log.info("Encontradas {} notícias iniciais no HTML.", result.size());
            return result;

        } catch (IOException e) {
            log.error("Falha ao raspar HTML inicial da página: {}", initialPageUrl, e);
            return result; // Retorna lista vazia em caso de falha
        }
    }

    /**
     * Busca notícias da API POST (código que você já tinha)
     *
     * @return Lista de Notícias parciais (URL e Título)
     */
    private List<News> fetchApiNews() {
        Map<String, Object> requestBody = Map.of(
                "post_id", this.postIdMercados,
                "categories", List.of(Constants.UM), //
                "tags", List.of()
        );

        log.info("Buscando lista de notícias da API via POST...");
        String jsonResponse = this.httpClient.makePostRequest(
                this.apiUrlMercados,
                requestBody,
                String.class,
                null,
                null
        );

        List<News> result = new ArrayList<>();
        try {
            JsonNode root = this.objectMapper.readTree(jsonResponse);
            for (JsonNode node : root) {
                String url = node.get("post_permalink").asText();
                String title = node.get("post_title").asText();
                result.add(new News(url, title));
            }
        } catch (Exception e) {
            log.error("Erro ao parsear JSON da API", e);
            throw new RuntimeException("Erro ao parsear JSON da API", e);
        }
        log.info("Encontradas {} notícias na API.", result.size());
        return result;
    }
}