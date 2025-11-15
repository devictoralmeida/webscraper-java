package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
@DisplayName("Testes para o serviço HtmlParser")
class HtmlParserImplTest {
    @InjectMocks
    private HtmlParserImpl htmlParser;
    private PartialNewsDTO partialNews;

    @BeforeEach
    void setUp() {
        this.partialNews = new PartialNewsDTO("https://example.com/news", "Título da Notícia");
    }

    @Nested
    @DisplayName("Testes para parseNewsDetails")
    class ParseNewsDetailsTests {

        @Test
        @DisplayName("Deve parsear notícia completa com todos os campos presentes")
        void deveParsearNoticiaCompletaComTodosCamposPresentes() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="article-title">
                                <div>Subtítulo da notícia</div>
                            </div>
                            <div data-ds-component="author-bio">
                                <a>João Silva</a>
                            </div>
                            <article data-ds-component="article">
                                <p>Conteúdo da notícia</p>
                            </article>
                            <time datetime="2024-01-15T10:30:00Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result).isNotNull();
            assertThat(result.getPartialNews()).isEqualTo(HtmlParserImplTest.this.partialNews);
            assertThat(result.getSubtitle()).isEqualTo("Subtítulo da notícia");
            assertThat(result.getAuthorName()).isEqualTo("João Silva");
            assertThat(result.getContent()).contains("Conteúdo da notícia");
            assertThat(result.getPublishDate()).isNotNull();
        }

        @Test
        @DisplayName("Deve parsear notícia com subtítulo nulo quando elemento não existe")
        void deveParsearNoticiaSemSubtitulo() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="author-bio">
                                <a>João Silva</a>
                            </div>
                            <article data-ds-component="article">
                                <p>Conteúdo</p>
                            </article>
                            <time datetime="2024-01-15T10:30:00Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getSubtitle()).isNull();
            assertThat(result.getAuthorName()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve parsear notícia com autor nulo quando elemento não existe")
        void deveParsearNoticiaSemAutor() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="article-title">
                                <div>Subtítulo</div>
                            </div>
                            <article data-ds-component="article">
                                <p>Conteúdo</p>
                            </article>
                            <time datetime="2024-01-15T10:30:00Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getSubtitle()).isEqualTo("Subtítulo");
            assertThat(result.getAuthorName()).isNull();
        }

        @Test
        @DisplayName("Deve parsear notícia com conteúdo vazio quando artigo não existe")
        void deveParsearNoticiaSemConteudo() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="article-title">
                                <div>Subtítulo</div>
                            </div>
                            <div data-ds-component="author-bio">
                                <a>João Silva</a>
                            </div>
                            <time datetime="2024-01-15T10:30:00Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Deve parsear notícia removendo elementos de anúncios do conteúdo")
        void deveParsearNoticiaRemovendoAnuncios() {
            String html = """
                    <html>
                        <body>
                            <article data-ds-component="article">
                                <p>Início do conteúdo</p>
                                <div data-ds-component="ad">Anúncio</div>
                                <p>Meio do conteúdo</p>
                                <div class="cta-middle">CTA</div>
                                <iframe src="ad.html"></iframe>
                                <div data-component-type="ads">Banner</div>
                                <p>Fim do conteúdo</p>
                            </article>
                            <time datetime="2024-01-15T10:30:00Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getContent()).contains("Início do conteúdo");
            assertThat(result.getContent()).contains("Meio do conteúdo");
            assertThat(result.getContent()).contains("Fim do conteúdo");
            assertThat(result.getContent()).doesNotContain("Anúncio");
            assertThat(result.getContent()).doesNotContain("CTA");
            assertThat(result.getContent()).doesNotContain("Banner");
        }

        @Test
        @DisplayName("Deve parsear notícia com data nula quando elemento time não existe")
        void deveParsearNoticiaSemData() {
            String html = """
                    <html>
                        <body>
                            <div data-ds-component="article-title">
                                <div>Subtítulo</div>
                            </div>
                            <article data-ds-component="article">
                                <p>Conteúdo</p>
                            </article>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getPublishDate()).isNull();
        }

        @Test
        @DisplayName("Deve parsear notícia com data nula quando atributo datetime está vazio")
        void deveParsearNoticiaComDatatimeVazio() {
            String html = """
                    <html>
                        <body>
                            <article data-ds-component="article">
                                <p>Conteúdo</p>
                            </article>
                            <time datetime=""></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getPublishDate()).isNull();
        }

        @Test
        @DisplayName("Deve parsear notícia com todos os campos opcionais nulos")
        void deveParsearNoticiaComTodosCamposNulos() {
            String html = """
                    <html>
                        <body>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result).isNotNull();
            assertThat(result.getPartialNews()).isEqualTo(HtmlParserImplTest.this.partialNews);
            assertThat(result.getSubtitle()).isNull();
            assertThat(result.getAuthorName()).isNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getPublishDate()).isNull();
        }

        @Test
        @DisplayName("Deve parsear notícia com data válida em formato ISO")
        void deveParsearNoticiaComDataValidaISO() {
            String html = """
                    <html>
                        <body>
                            <article data-ds-component="article">
                                <p>Conteúdo</p>
                            </article>
                            <time datetime="2024-03-20T15:45:30Z"></time>
                        </body>
                    </html>
                    """;

            ParsedNewsDTO result = HtmlParserImplTest.this.htmlParser.parseNewsDetails(html, HtmlParserImplTest.this.partialNews);

            assertThat(result.getPublishDate()).isNotNull();
            assertThat(result.getPublishDate()).isInstanceOf(LocalDateTime.class);
        }
    }

    @Nested
    @DisplayName("Testes para parseHtmlContent")
    class ParseHtmlContentTests {

        @Test
        @DisplayName("Deve parsear HTML válido e retornar Document")
        void deveParsearHtmlValidoERetornarDocument() {
            String html = "<html><body><h1>Título</h1></body></html>";

            Document result = HtmlParserImplTest.this.htmlParser.parseHtmlContent(html);

            assertThat(result).isNotNull();
            assertThat(result.select("h1").text()).isEqualTo("Título");
        }

        @Test
        @DisplayName("Deve parsear HTML vazio e retornar Document")
        void deveParsearHtmlVazioERetornarDocument() {
            String html = "";

            Document result = HtmlParserImplTest.this.htmlParser.parseHtmlContent(html);

            assertThat(result).isNotNull();
            assertThat(result.body()).isNotNull();
        }

        @Test
        @DisplayName("Deve parsear HTML malformado e retornar Document")
        void deveParsearHtmlMalformadoERetornarDocument() {
            String html = "<html><body><p>Parágrafo sem fechamento</body></html>";

            Document result = HtmlParserImplTest.this.htmlParser.parseHtmlContent(html);

            assertThat(result).isNotNull();
            assertThat(result.body()).isNotNull();
        }

        @Test
        @DisplayName("Deve parsear HTML complexo com múltiplos elementos")
        void deveParsearHtmlComplexoComMultiplosElementos() {
            String html = """
                    <html>
                        <head><title>Teste</title></head>
                        <body>
                            <div class="container">
                                <article>
                                    <h1>Título</h1>
                                    <p>Parágrafo 1</p>
                                    <p>Parágrafo 2</p>
                                </article>
                            </div>
                        </body>
                    </html>
                    """;

            Document result = HtmlParserImplTest.this.htmlParser.parseHtmlContent(html);

            assertThat(result).isNotNull();
            assertThat(result.select("article")).isNotEmpty();
            assertThat(result.select("p")).hasSize(2);
        }

        @Test
        @DisplayName("Deve parsear HTML com caracteres especiais")
        void deveParsearHtmlComCaracteresEspeciais() {
            String html = "<html><body><p>Texto com &amp; &lt; &gt; caracteres especiais</p></body></html>";

            Document result = HtmlParserImplTest.this.htmlParser.parseHtmlContent(html);

            assertThat(result).isNotNull();
            assertThat(result.select("p").text()).contains("&");
        }
    }

    public static Stream<String> provideInvalidHtmlScenarios() {
        return Stream.of(
                null
        );
    }
}
