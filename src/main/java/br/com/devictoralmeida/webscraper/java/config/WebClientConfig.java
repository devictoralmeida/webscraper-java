package br.com.devictoralmeida.webscraper.java.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${api.baseurl}")
    private String apiBaseUrl;

    @Value("${user.agent}")
    private String userAgent;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
                .build();
    }
}
