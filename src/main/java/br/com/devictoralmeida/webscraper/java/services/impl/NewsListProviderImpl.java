package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsListProviderImpl implements NewsListProvider {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${api.url.mercados}")
    private String apiUrlMercados;


    @Value("${post.id.mercados}")
    private String postIdMercados;


    @Override
    public List<News> fetchNewsList(int pageLimit) {
        Map<String, Object> requestBody = getRequestBody();

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

            // Adapte a navegação do JSON conforme a resposta real da API
            int count = 0;
            for (JsonNode node : root) {
                if (count >= pageLimit) {
                    break;
                }

                String url = node.get("post_permalink").asText();
                String title = node.get("post_title").asText();

                result.add(new News(url, title));
                count++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao parsear JSON da API", e);
        }
        log.info("Encontradas {} notícias para processar.", result.size());
        return result;
    }

    private Map<String, Object> getRequestBody() {
        return Map.of(
                "post_id", this.postIdMercados,
                "categories", List.of(Constants.UM),
                "tags", List.of()
        );
    }
}
