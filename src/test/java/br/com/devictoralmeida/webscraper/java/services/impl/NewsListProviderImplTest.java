package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.exception.NegocioException;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o serviço NewsListProvider")
class NewsListProviderImplTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HtmlParser parser;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private NewsListProviderImpl newsListProvider;

    private static final String BASE_URL = "https://example.com";
    private static final String API_URL = "https://api.example.com/news";
    private static final String POST_ID = "12345";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(this.newsListProvider, "baseUrl", BASE_URL);
        ReflectionTestUtils.setField(this.newsListProvider, "apiUrlMercados", API_URL);
        ReflectionTestUtils.setField(this.newsListProvider, "postIdMercados", POST_ID);
    }

    @Nested
    @DisplayName("Testes para fetchNewsList")
    class FetchNewsListTests {

        @Test
        @DisplayName("Deve retornar apenas notícias iniciais quando pageLimit é menor ou igual ao tamanho inicial")
        void deveRetornarApenasNoticiasIniciaisQuandoLimiteAtingido() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                            <div data-ds-component="card-sm">
                                <h2><a href="/noticia2">Título 2</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(2);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getUrl()).isEqualTo("/noticia1");
            assertThat(result.get(0).getTitle()).isEqualTo("Título 1");
            verify(NewsListProviderImplTest.this.httpClient, times(1)).makeGetRequest(anyString(), eq(String.class), isNull(), isNull());
            verify(NewsListProviderImplTest.this.httpClient, never()).makePostRequest(anyString(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve buscar notícias da API quando pageLimit é maior que notícias iniciais")
        void deveBuscarNoticiasDaApiQuandoLimiteMaiorQueInicial() throws Exception {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            String jsonResponse = """
                    [
                        {"post_permalink": "/noticia2", "post_title": "Título 2"},
                        {"post_permalink": "/noticia3", "post_title": "Título 3"}
                    ]
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull())).thenReturn(jsonResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(3);

            assertThat(result).hasSize(3);
            verify(NewsListProviderImplTest.this.httpClient, times(1)).makeGetRequest(anyString(), eq(String.class), isNull(), isNull());
            verify(NewsListProviderImplTest.this.httpClient, times(1)).makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull());
        }

        @Test
        @DisplayName("Deve remover notícias duplicadas entre HTML e API")
        void deveRemoverNoticiasDuplicadas() throws Exception {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            String jsonResponse = """
                    [
                        {"post_permalink": "/noticia1", "post_title": "Título 1"},
                        {"post_permalink": "/noticia2", "post_title": "Título 2"}
                    ]
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull())).thenReturn(jsonResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(5);

            assertThat(result).hasSize(2);
            assertThat(result.stream().filter(n -> n.getUrl().equals("/noticia1")).count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Deve respeitar o pageLimit mesmo com mais notícias disponíveis")
        void deveRespeitarPageLimit() throws Exception {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            String jsonResponse = """
                    [
                        {"post_permalink": "/noticia2", "post_title": "Título 2"},
                        {"post_permalink": "/noticia3", "post_title": "Título 3"},
                        {"post_permalink": "/noticia4", "post_title": "Título 4"}
                    ]
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull())).thenReturn(jsonResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(2);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Deve sanitizar URLs removendo fragmentos (#)")
        void deveSanitizarUrlsRemovendoFragmentos() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1#fragmento">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(1);

            assertThat(result.get(0).getUrl()).isEqualTo("/noticia1");
        }

        @Test
        @DisplayName("Deve filtrar links vazios ou sem href")
        void deveFiltrarLinksVazios() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Notícia 1</a></h2>
                            </div>
                            <div data-ds-component="card-sm">
                                <h2><a href="">Notícia sem href</a></h2>
                            </div>
                            <div data-ds-component="card-xl">
                                <h2><a>Notícia sem atributo href</a></h2>
                            </div>
                            <div class="related-link">
                                <a href="/noticia2">Notícia 2</a>
                            </div>
                        </body>
                    </html>
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(2);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(PartialNewsDTO::getUrl)
                    .containsExactlyInAnyOrder("/noticia1", "/noticia2");
        }

        @Test
        @DisplayName("Deve buscar notícias de diferentes tipos de cards")
        void deveBuscarNoticiasDeDiferentesTiposDeCards() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia-xl">Notícia XL</a></h2>
                            </div>
                            <div data-ds-component="card-sm">
                                <h2><a href="/noticia-sm">Notícia SM</a></h2>
                            </div>
                            <div class="related-link">
                                <a href="/noticia-related">Notícia Related</a>
                            </div>
                        </body>
                    </html>
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(3);

            assertThat(result).hasSize(3);
            assertThat(result).extracting(PartialNewsDTO::getUrl)
                    .containsExactlyInAnyOrder("/noticia-xl", "/noticia-sm", "/noticia-related");
        }

        @Test
        @DisplayName("Deve lançar NegocioException quando ocorrer erro ao buscar HTML")
        void deveLancarExcecaoQuandoErroAoBuscarHtml() {
            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenThrow(new RuntimeException("Erro de rede"));

            assertThatThrownBy(() -> NewsListProviderImplTest.this.newsListProvider.fetchNewsList(10))
                    .isInstanceOf(NegocioException.class)
                    .hasMessageContaining("Falha ao processar HTML da página inicial");
        }

        @Test
        @DisplayName("Deve lançar NegocioException quando ocorrer erro ao parsear JSON da API")
        void deveLancarExcecaoQuandoErroAoParsearJsonApi() throws Exception {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull()))
                    .thenReturn("invalid json");
            doThrow(new RuntimeException("Erro ao parsear JSON")).when(NewsListProviderImplTest.this.objectMapper).readTree(anyString());

            assertThatThrownBy(() -> NewsListProviderImplTest.this.newsListProvider.fetchNewsList(10))
                    .isInstanceOf(NegocioException.class)
                    .hasMessageContaining("Erro durante o parse do JSON da API");
        }

        @Test
        @DisplayName("Deve filtrar notícias da API com campos vazios")
        void deveFiltrarNoticiasDaApiComCamposVazios() throws Exception {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="card-xl">
                                <h2><a href="/noticia1">Título 1</a></h2>
                            </div>
                        </body>
                    </html>
                    """;

            String jsonResponse = """
                    [
                        {"post_permalink": "", "post_title": "Título Vazio"},
                        {"post_permalink": "/noticia2", "post_title": ""},
                        {"post_permalink": "/noticia3", "post_title": "Título 3"}
                    ]
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull())).thenReturn(jsonResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(10);

            assertThat(result).hasSize(2);
            assertThat(result.stream().anyMatch(n -> n.getTitle().equals("Título 3"))).isTrue();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há notícias disponíveis")
        void deveRetornarListaVaziaQuandoNaoHaNoticias() {
            String html = """
                    <html>
                        <body>
                            <div>Nenhuma notícia disponível</div>
                        </body>
                    </html>
                    """;

            String apiResponse = "[]";

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);
            when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), anyMap(), eq(String.class), isNull(), isNull())).thenReturn(apiResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(10);

            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("br.com.devictoralmeida.webscraper.java.services.impl.NewsListProviderImplTest#providePageLimits")
        @DisplayName("Deve respeitar diferentes valores de pageLimit")
        void deveRespeitarDiferentesValoresDePageLimit(int pageLimit) {
            String html = """
                    <html>
                        <div data-ds-component='card-xl'><h2><a href='/noticia1'>Notícia 1</a></h2></div>
                        <div data-ds-component='card-sm'><h2><a href='/noticia2'>Notícia 2</a></h2></div>
                        <div class='related-link'><a href='/noticia3'>Notícia 3</a></div>
                    </html>
                    """;

            String jsonResponse = """
                    [
                        {"post_permalink": "/noticia4", "post_title": "Notícia 4"},
                        {"post_permalink": "/noticia5", "post_title": "Notícia 5"}
                    ]
                    """;

            Document doc = Jsoup.parse(html);

            when(NewsListProviderImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull())).thenReturn(html);
            when(NewsListProviderImplTest.this.parser.parseHtmlContent(html)).thenReturn(doc);

            // Usar lenient() para permitir que o stub não seja usado
            lenient().when(NewsListProviderImplTest.this.httpClient.makePostRequest(anyString(), any(), eq(String.class), isNull(), isNull())).thenReturn(jsonResponse);

            List<PartialNewsDTO> result = NewsListProviderImplTest.this.newsListProvider.fetchNewsList(pageLimit);

            assertThat(result).hasSize(Math.min(pageLimit, 5));
        }

    }

    public static Stream<Integer> providePageLimits() {
        return Stream.of(1, 2, 5, 10, 50);
    }
}
