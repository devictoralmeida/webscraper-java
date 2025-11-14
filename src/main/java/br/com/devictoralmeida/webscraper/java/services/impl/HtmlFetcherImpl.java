package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.services.HtmlFetcher;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HtmlFetcherImpl implements HtmlFetcher {
    private final HttpClient httpClient;

    @Override
    public String fetchHtmlContent(String url) {
        return httpClient.makeGetRequest(url, String.class, null, null);
    }
}
