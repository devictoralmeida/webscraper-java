package br.com.devictoralmeida.webscraper.java.services;

import java.util.Map;

public interface HttpClient {
    <T> T makeGetRequest(String url, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers);

    <T> T makePostRequest(String url, Object body, Class<T> responseType, Map<String, String> queryParams, Map<String, String> headers);
}
