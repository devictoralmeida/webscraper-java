package br.com.devictoralmeida.webscraper.java.config;

import br.com.devictoralmeida.webscraper.java.shared.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${api.baseurl}")
    private String apiBaseUrl;

    @Value("${user.agent}")
    private String userAgent;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(Constants.DEZ_MB))
                        .build())
                .baseUrl(this.apiBaseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, this.userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
    }
}
