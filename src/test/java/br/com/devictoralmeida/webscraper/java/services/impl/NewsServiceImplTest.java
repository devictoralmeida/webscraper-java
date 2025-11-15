package br.com.devictoralmeida.webscraper.java.services.impl;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.entities.News;
import br.com.devictoralmeida.webscraper.java.exception.ParametrosDeConsultaInvalidosException;
import br.com.devictoralmeida.webscraper.java.exception.RecursoNaoEncontradoException;
import br.com.devictoralmeida.webscraper.java.repositories.AuthorRepository;
import br.com.devictoralmeida.webscraper.java.repositories.NewsRepository;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o serviço NewsService")
class NewsServiceImplTest {

    @Mock
    private ScraperService scrapingService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsServiceImpl newsService;

    private DateRangeRequestDTO dateRangeRequest;
    private Author author;
    private News news;
    private AuthorNewsCountResponseDTO authorNewsCount;

    @BeforeEach
    void setUp() {
        this.dateRangeRequest = new DateRangeRequestDTO(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );

        this.author = new Author("João Silva");
        this.author.setId(1L);

        this.news = new News();
        this.news.setId(1L);
        this.news.setTitle("Título da Notícia");
        this.news.setUrl("/noticia-teste");
        this.news.setSubtitle("Subtítulo");
        this.news.setContent("Conteúdo da notícia");
        this.news.setPublishDate(LocalDateTime.of(2024, 6, 15, 10, 0));
        this.news.setAuthor(this.author);

        this.authorNewsCount = new AuthorNewsCountResponseDTO(1L, "João Silva", 10L);
    }

    @Nested
    @DisplayName("Testes para scrapeAndSaveNews")
    class ScrapeAndSaveNewsTests {

        @Test
        @DisplayName("Deve executar scraping com sucesso")
        void deveExecutarScrapingComSucesso() {
            NewsResponseDTO newsResponse = new NewsResponseDTO(NewsServiceImplTest.this.news);
            when(NewsServiceImplTest.this.scrapingService.execute(anyInt()))
                    .thenReturn(List.of(newsResponse));

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService.scrapeAndSaveNews(15);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título da Notícia");
            verify(NewsServiceImplTest.this.scrapingService, times(1)).execute(15);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há notícias novas")
        void deveRetornarListaVaziaQuandoNaoHaNoticiasNovas() {
            when(NewsServiceImplTest.this.scrapingService.execute(anyInt()))
                    .thenReturn(List.of());

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService.scrapeAndSaveNews(15);

            assertThat(result).isEmpty();
            verify(NewsServiceImplTest.this.scrapingService, times(1)).execute(15);
        }

        @Test
        @DisplayName("Deve processar múltiplas notícias com sucesso")
        void deveProcessarMultiplasNoticiasComSucesso() {
            News news2 = new News();
            news2.setId(2L);
            news2.setTitle("Segunda Notícia");
            news2.setUrl("/noticia-2");
            news2.setSubtitle("Subtítulo 2");
            news2.setContent("Conteúdo 2");
            news2.setPublishDate(LocalDateTime.of(2024, 6, 16, 11, 0));
            news2.setAuthor(NewsServiceImplTest.this.author);

            NewsResponseDTO newsResponse1 = new NewsResponseDTO(NewsServiceImplTest.this.news);
            NewsResponseDTO newsResponse2 = new NewsResponseDTO(news2);

            when(NewsServiceImplTest.this.scrapingService.execute(anyInt()))
                    .thenReturn(List.of(newsResponse1, newsResponse2));

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService.scrapeAndSaveNews(20);

            assertThat(result).hasSize(2);
            verify(NewsServiceImplTest.this.scrapingService, times(1)).execute(20);
        }
    }

    @Nested
    @DisplayName("Testes para findTopAuthorsByDateRange")
    class FindTopAuthorsByDateRangeTests {

        @Test
        @DisplayName("Deve buscar autores mais ativos com sucesso")
        void deveBuscarAutoresMaisAtivosComSucesso() {
            when(NewsServiceImplTest.this.authorRepository.findAuthorsWithMostPublicationsOnDateRange(any(), any()))
                    .thenReturn(List.of(NewsServiceImplTest.this.authorNewsCount));

            List<AuthorNewsCountResponseDTO> result = NewsServiceImplTest.this.newsService
                    .findTopAuthorsByDateRange(NewsServiceImplTest.this.dateRangeRequest);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("João Silva");
            assertThat(result.get(0).getNewsCount()).isEqualTo(10L);
            verify(NewsServiceImplTest.this.authorRepository, times(1))
                    .findAuthorsWithMostPublicationsOnDateRange(any(), any());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há autores no período")
        void deveRetornarListaVaziaQuandoNaoHaAutoresNoPeriodo() {
            when(NewsServiceImplTest.this.authorRepository.findAuthorsWithMostPublicationsOnDateRange(any(), any()))
                    .thenReturn(List.of());

            List<AuthorNewsCountResponseDTO> result = NewsServiceImplTest.this.newsService
                    .findTopAuthorsByDateRange(NewsServiceImplTest.this.dateRangeRequest);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção quando data início é posterior à data fim")
        void deveLancarExcecaoQuandoDataInicioEPosteriorAFim() {
            DateRangeRequestDTO invalidRequest = new DateRangeRequestDTO(
                    LocalDateTime.of(2024, 12, 31, 23, 59),
                    LocalDateTime.of(2024, 1, 1, 0, 0)
            );

            assertThatThrownBy(() -> NewsServiceImplTest.this.newsService.findTopAuthorsByDateRange(invalidRequest))
                    .isInstanceOf(ParametrosDeConsultaInvalidosException.class);
        }
    }

    @Nested
    @DisplayName("Testes para findNewsByAuthorAndDateRange")
    class FindNewsByAuthorAndDateRangeTests {

        @Test
        @DisplayName("Deve buscar notícias de um autor específico com sucesso")
        void deveBuscarNoticiasDeAutorEspecificoComSucesso() {
            when(NewsServiceImplTest.this.authorRepository.existsById(anyLong()))
                    .thenReturn(true);
            when(NewsServiceImplTest.this.newsRepository.findNewsByAuthorAndDateRange(anyLong(), any(), any()))
                    .thenReturn(List.of(NewsServiceImplTest.this.news));

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService
                    .findNewsByAuthorAndDateRange(1L, NewsServiceImplTest.this.dateRangeRequest);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Título da Notícia");
            verify(NewsServiceImplTest.this.authorRepository, times(1)).existsById(1L);
            verify(NewsServiceImplTest.this.newsRepository, times(1))
                    .findNewsByAuthorAndDateRange(anyLong(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando autor não tem notícias no período")
        void deveRetornarListaVaziaQuandoAutorNaoTemNoticiasNoPeriodo() {
            when(NewsServiceImplTest.this.authorRepository.existsById(anyLong()))
                    .thenReturn(true);
            when(NewsServiceImplTest.this.newsRepository.findNewsByAuthorAndDateRange(anyLong(), any(), any()))
                    .thenReturn(List.of());

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService
                    .findNewsByAuthorAndDateRange(1L, NewsServiceImplTest.this.dateRangeRequest);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção quando autor não existe")
        void deveLancarExcecaoQuandoAutorNaoExiste() {
            when(NewsServiceImplTest.this.authorRepository.existsById(anyLong()))
                    .thenReturn(false);

            assertThatThrownBy(() -> NewsServiceImplTest.this.newsService
                    .findNewsByAuthorAndDateRange(999L, NewsServiceImplTest.this.dateRangeRequest))
                    .isInstanceOf(RecursoNaoEncontradoException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção quando data início é posterior à data fim")
        void deveLancarExcecaoQuandoDataInicioEPosteriorAFim() {
            DateRangeRequestDTO invalidRequest = new DateRangeRequestDTO(
                    LocalDateTime.of(2024, 12, 31, 23, 59),
                    LocalDateTime.of(2024, 1, 1, 0, 0)
            );

            when(NewsServiceImplTest.this.authorRepository.existsById(anyLong()))
                    .thenReturn(true);

            assertThatThrownBy(() -> NewsServiceImplTest.this.newsService
                    .findNewsByAuthorAndDateRange(1L, invalidRequest))
                    .isInstanceOf(ParametrosDeConsultaInvalidosException.class);
        }

        @Test
        @DisplayName("Deve buscar múltiplas notícias de um autor")
        void deveBuscarMultiplasNoticiasDeUmAutor() {
            News news2 = new News();
            news2.setId(2L);
            news2.setTitle("Segunda Notícia");
            news2.setUrl("/noticia-2");
            news2.setSubtitle("Subtítulo 2");
            news2.setContent("Conteúdo 2");
            news2.setPublishDate(LocalDateTime.of(2024, 6, 16, 11, 0));
            news2.setAuthor(NewsServiceImplTest.this.author);

            when(NewsServiceImplTest.this.authorRepository.existsById(anyLong()))
                    .thenReturn(true);
            when(NewsServiceImplTest.this.newsRepository.findNewsByAuthorAndDateRange(anyLong(), any(), any()))
                    .thenReturn(List.of(NewsServiceImplTest.this.news, news2));

            List<NewsResponseDTO> result = NewsServiceImplTest.this.newsService
                    .findNewsByAuthorAndDateRange(1L, NewsServiceImplTest.this.dateRangeRequest);

            assertThat(result).hasSize(2);
        }
    }
}
