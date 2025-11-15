package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.HtmlParser;
import br.com.devictoralmeida.webscraper.java.services.HttpClient;
import br.com.devictoralmeida.webscraper.java.services.NewsListProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
@DisplayName("Testes para o serviço ScraperService")
class ScraperServiceImplTest {

    @Mock
    private NewsListProvider listProvider;

    @Mock
    private HtmlParser parser;

    @Mock
    private NewsRepository repository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private ScraperServiceImpl scraperService;

    private PartialNewsDTO partialNews1;
    private PartialNewsDTO partialNews2;
    private ParsedNewsDTO parsedNews1;
    private ParsedNewsDTO parsedNews2;
    private Author author1;
    private Author author2;
    private News news1;
    private News news2;

    @BeforeEach
    void setUp() {
        this.partialNews1 = new PartialNewsDTO("/noticia1", "Título 1");
        this.partialNews2 = new PartialNewsDTO("/noticia2", "Título 2");

        this.author1 = new Author("João Silva");
        this.author1.setId(1L);
        this.author2 = new Author("Maria Santos");
        this.author2.setId(2L);

        this.parsedNews1 = new ParsedNewsDTO(
                this.partialNews1,
                "Subtítulo 1",
                "Conteúdo 1",
                LocalDateTime.of(2024, 1, 15, 10, 0),
                "João Silva"
        );

        this.parsedNews2 = new ParsedNewsDTO(
                this.partialNews2,
                "Subtítulo 2",
                "Conteúdo 2",
                LocalDateTime.of(2024, 1, 16, 11, 0),
                "Maria Santos"
        );

        this.news1 = new News(this.parsedNews1, this.author1);
        this.news1.setId(1L);
        this.news2 = new News(this.parsedNews2, this.author2);
        this.news2.setId(2L);
    }

    @Nested
    @DisplayName("Testes para execute")
    class ExecuteTests {

        @Test
        @DisplayName("Deve processar e salvar notícias novas com sucesso")
        void deveProcessarESalvarNoticiasNovasComSucesso() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews1, ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news1, ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(2);
            verify(ScraperServiceImplTest.this.listProvider, times(1)).fetchNewsList(10);
            verify(ScraperServiceImplTest.this.repository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando todas as notícias já existem")
        void deveRetornarListaVaziaQuandoTodasNoticiasJaExistem() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of("/noticia1", "/noticia2"));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).isEmpty();
            verify(ScraperServiceImplTest.this.httpClient, never()).makeGetRequest(anyString(), any(), any(), any());
            verify(ScraperServiceImplTest.this.repository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Deve filtrar notícias que já existem e processar apenas as novas")
        void deveFiltrarNoticiasExistentesEProcessarApenasNovas() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of("/noticia1"));
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título 2");
        }

        @Test
        @DisplayName("Deve reutilizar autores existentes ao processar notícias")
        void deveReutilizarAutoresExistentes() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews1);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(Set.of("João Silva")))
                    .thenReturn(List.of(ScraperServiceImplTest.this.author1));
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news1));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            verify(ScraperServiceImplTest.this.authorRepository, times(1)).findByNameIn(anySet());
            verify(ScraperServiceImplTest.this.authorRepository, never()).save(any(Author.class));
        }

        @Test
        @DisplayName("Deve criar novos autores quando não existem no banco")
        void deveCriarNovosAutoresQuandoNaoExistem() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews1);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news1));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            verify(ScraperServiceImplTest.this.authorRepository, times(1)).findByNameIn(anySet());
        }

        @Test
        @DisplayName("Deve filtrar notícias sem data de publicação")
        void deveFiltrarNoticiasSemDataDePublicacao() {
            ParsedNewsDTO parsedSemData = new ParsedNewsDTO(
                    ScraperServiceImplTest.this.partialNews1,
                    "Subtítulo",
                    "Conteúdo",
                    null,
                    "João Silva"
            );

            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(parsedSemData, ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título 2");
        }

        @Test
        @DisplayName("Deve filtrar notícias sem autor")
        void deveFiltrarNoticiasSemAutor() {
            ParsedNewsDTO parsedSemAutor = new ParsedNewsDTO(
                    ScraperServiceImplTest.this.partialNews1,
                    "Subtítulo",
                    "Conteúdo",
                    LocalDateTime.now(),
                    null
            );

            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(parsedSemAutor, ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título 2");
        }

        @Test
        @DisplayName("Deve continuar processamento mesmo quando uma notícia falha")
        void deveContinuarProcessamentoQuandoUmaNoticiaFalha() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(eq("/noticia1"), eq(String.class), isNull(), isNull()))
                    .thenThrow(new RuntimeException("Erro ao buscar HTML"));
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(eq("/noticia2"), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título 2");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há notícias para processar")
        void deveRetornarListaVaziaQuandoNaoHaNoticiasParaProcessar() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of());

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).isEmpty();
            verify(ScraperServiceImplTest.this.repository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("Deve processar múltiplas notícias com diferentes autores")
        void deveProcessarMultiplasNoticiasComDiferentesAutores() {
            when(ScraperServiceImplTest.this.listProvider.fetchNewsList(anyInt()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.partialNews1, ScraperServiceImplTest.this.partialNews2));
            when(ScraperServiceImplTest.this.repository.findUrlsIn(anyList()))
                    .thenReturn(List.of());
            when(ScraperServiceImplTest.this.httpClient.makeGetRequest(anyString(), eq(String.class), isNull(), isNull()))
                    .thenReturn("<html><body>HTML Content</body></html>");
            when(ScraperServiceImplTest.this.parser.parseNewsDetails(anyString(), any(PartialNewsDTO.class)))
                    .thenReturn(ScraperServiceImplTest.this.parsedNews1, ScraperServiceImplTest.this.parsedNews2);
            when(ScraperServiceImplTest.this.authorRepository.findByNameIn(anySet()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.author1));
            when(ScraperServiceImplTest.this.repository.saveAll(anyList()))
                    .thenReturn(List.of(ScraperServiceImplTest.this.news1, ScraperServiceImplTest.this.news2));

            List<NewsResponseDTO> result = ScraperServiceImplTest.this.scraperService.execute(10);

            assertThat(result).hasSize(2);
            verify(ScraperServiceImplTest.this.authorRepository, times(1)).findByNameIn(anySet());
        }
    }
}
