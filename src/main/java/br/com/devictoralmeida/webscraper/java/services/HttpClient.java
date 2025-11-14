package br.com.devictoralmeida.webscraper.java.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface HttpClient {
    @Transactional
    <T> T makeGetRequest(String url, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers);

    @Transactional
    <T> T makePostRequest(String url, Object body, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers);

    @Transactional
    <T> T makePostRequest(String uri, Object body, ParameterizedTypeReference<T> responseType, Map<String, String> queryParams, Map<String, String> headers);
}
