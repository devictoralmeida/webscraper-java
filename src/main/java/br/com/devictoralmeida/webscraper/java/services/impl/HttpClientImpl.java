package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class HttpClientImpl implements HttpClient {
    private final Logger log = LoggerFactory.getLogger(HttpClientImpl.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public HttpClientImpl(WebClient.Builder webClient, ObjectMapper objectMapper) {
        this.webClient = webClient.build();
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> T makeGetRequest(String url, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers) {
        try {
            return this.webClient.get()
                    .uri(buildUri(url, queryParams).build().toUri())
                    .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (Exception exception) {
            this.log.error("Erro durante requisição de GET para url: {}, erro: {}", url, exception.getMessage());
            throw new RuntimeException("Error during request: " + exception.getMessage(), exception);
        }
    }

    @Override
    public <T> T makePostRequest(String url, Object body, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers) {
        try {
            Object response = this.webClient.post()
                    .uri(buildUri(url, queryParams).build().toUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
            return this.objectMapper.convertValue(response, responseType);
        } catch (Exception e) {
            this.log.error("Erro durante requisição de POST para url: {}, erro: {}", url, e.getMessage());
            throw new RuntimeException("Error during request: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T makePostRequest(String url, Object body, ParameterizedTypeReference<T> responseType, Map<String, String> queryParams, Map<String, String> headers) {
        try {
            return this.webClient.post()
                    .uri(buildUri(url, queryParams).build().toUri())
                    .headers(httpHeaders -> addHeaders(httpHeaders, headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (Exception e) {
            this.log.error("Erro durante requisição de GET para url: {}, erro: {}", url, e.getMessage());
            throw new RuntimeException("Error during request: " + e.getMessage(), e);
        }
    }

    private UriComponentsBuilder buildUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

        if (queryParams != null) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        return uriBuilder;
    }

    private void addHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
    }
}
