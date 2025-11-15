package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@DisplayName("testes para o serviço HttpClient")
class HttpClientImplTest {
    private final String url = "http://example.com";
    private final String responseBody = "response";
    private final String body = "body";
    private final Map<String, String> queryParams = Map.of("key", "value");
    private final Map<String, String> headers = Map.of("headerKey", "headerValue");

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        this.httpClient = new HttpClientImpl(this.webClientBuilder, this.objectMapper);
        ReflectionTestUtils.setField(this.httpClient, "webClient", this.webClient);
    }

    @Nested
    @DisplayName("Testes para o método makeGetRequest")
    class MakeGetRequest {
        @Test
        @DisplayName("Deve fazer uma requisição GET com sucesso")
        void testMakeGetRequest_success() {
            when(HttpClientImplTest.this.webClient.get()).thenReturn(HttpClientImplTest.this.requestHeadersUriSpec);
            when(HttpClientImplTest.this.requestHeadersUriSpec.uri((URI) any())).thenReturn(HttpClientImplTest.this.requestHeadersSpec);
            when(HttpClientImplTest.this.requestHeadersSpec.headers(any())).thenReturn(HttpClientImplTest.this.requestHeadersSpec);
            when(HttpClientImplTest.this.requestHeadersSpec.retrieve()).thenReturn(HttpClientImplTest.this.responseSpec);
            when(HttpClientImplTest.this.responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(HttpClientImplTest.this.responseBody));

            String result = HttpClientImplTest.this.httpClient.makeGetRequest(HttpClientImplTest.this.url, String.class, HttpClientImplTest.this.queryParams, HttpClientImplTest.this.headers);

            assertEquals(HttpClientImplTest.this.responseBody, result);
            verify(HttpClientImplTest.this.webClient, times(1)).get();
        }
    }
}