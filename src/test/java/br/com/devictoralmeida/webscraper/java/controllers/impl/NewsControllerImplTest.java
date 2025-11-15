package br.com.devictoralmeida.webscraper.java.controllers.impl;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.AuthorNewsCountResponseDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.NewsResponseDTO;
import br.com.devictoralmeida.webscraper.java.exception.RecursoNaoEncontradoException;
import br.com.devictoralmeida.webscraper.java.services.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o controller NewsController")
class NewsControllerImplTest {
    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsControllerImpl newsController;

    private DateRangeRequestDTO dateRangeRequest;
    private NewsResponseDTO newsResponse;
    private AuthorNewsCountResponseDTO authorNewsCount;

    @BeforeEach
    void setUp() {
        this.dateRangeRequest = new DateRangeRequestDTO(
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 12, 31, 23, 59)
        );

        this.newsResponse = mock(NewsResponseDTO.class);
        this.authorNewsCount = new AuthorNewsCountResponseDTO(1L, "João Silva", 10L);
    }

    @Nested
    @DisplayName("Testes para fetchNews")
    class FetchNewsTests {
        @Test
        @DisplayName("Deve buscar notícias com sucesso usando limite padrão")
        void deveBuscarNoticiasComSucessoUsandoLimitePadrao() {
            when(NewsControllerImplTest.this.newsService.scrapeAndSaveNews(15))
                    .thenReturn(List.of(NewsControllerImplTest.this.newsResponse));

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController.fetchNews(15);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            verify(NewsControllerImplTest.this.newsService, times(1)).scrapeAndSaveNews(15);
        }

        @Test
        @DisplayName("Deve buscar notícias com limite customizado")
        void deveBuscarNoticiasComLimiteCustomizado() {
            when(NewsControllerImplTest.this.newsService.scrapeAndSaveNews(50))
                    .thenReturn(List.of(NewsControllerImplTest.this.newsResponse));

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController.fetchNews(50);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(NewsControllerImplTest.this.newsService, times(1)).scrapeAndSaveNews(50);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há notícias novas")
        void deveRetornarListaVaziaQuandoNaoHaNoticiasNovas() {
            when(NewsControllerImplTest.this.newsService.scrapeAndSaveNews(anyInt()))
                    .thenReturn(List.of());

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController.fetchNews(15);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(NewsControllerImplTest.this.newsService, times(1)).scrapeAndSaveNews(15);
        }
    }

    @Nested
    @DisplayName("Testes para getTopAuthorsByDateRange")
    class GetTopAuthorsByDateRangeTests {
        @Test
        @DisplayName("Deve buscar autores mais ativos com sucesso")
        void deveBuscarAutoresMaisAtivosComSucesso() {
            when(NewsControllerImplTest.this.newsService.findTopAuthorsByDateRange(any(DateRangeRequestDTO.class)))
                    .thenReturn(List.of(NewsControllerImplTest.this.authorNewsCount));

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController
                    .getTopAuthorsByDateRange(NewsControllerImplTest.this.dateRangeRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            verify(NewsControllerImplTest.this.newsService, times(1))
                    .findTopAuthorsByDateRange(any(DateRangeRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há autores no período")
        void deveRetornarListaVaziaQuandoNaoHaAutoresNoPeriodo() {
            when(NewsControllerImplTest.this.newsService.findTopAuthorsByDateRange(any(DateRangeRequestDTO.class)))
                    .thenReturn(List.of());

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController
                    .getTopAuthorsByDateRange(NewsControllerImplTest.this.dateRangeRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(NewsControllerImplTest.this.newsService, times(1))
                    .findTopAuthorsByDateRange(any(DateRangeRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("Testes para getNewsByAuthorAndDateRange")
    class GetNewsByAuthorAndDateRangeTests {
        @Test
        @DisplayName("Deve buscar notícias de um autor específico com sucesso")
        void deveBuscarNoticiasDeAutorEspecificoComSucesso() {
            Long authorId = 1L;
            when(NewsControllerImplTest.this.newsService.findNewsByAuthorAndDateRange(anyLong(), any(DateRangeRequestDTO.class)))
                    .thenReturn(List.of(NewsControllerImplTest.this.newsResponse));

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController
                    .getNewsByAuthorAndDateRange(authorId, NewsControllerImplTest.this.dateRangeRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            verify(NewsControllerImplTest.this.newsService, times(1))
                    .findNewsByAuthorAndDateRange(eq(authorId), any(DateRangeRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando autor não tem notícias no período")
        void deveRetornarListaVaziaQuandoAutorNaoTemNoticiasNoPeriodo() {
            Long authorId = 1L;
            when(NewsControllerImplTest.this.newsService.findNewsByAuthorAndDateRange(anyLong(), any(DateRangeRequestDTO.class)))
                    .thenReturn(List.of());

            ResponseEntity<?> response = NewsControllerImplTest.this.newsController
                    .getNewsByAuthorAndDateRange(authorId, NewsControllerImplTest.this.dateRangeRequest);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(NewsControllerImplTest.this.newsService, times(1))
                    .findNewsByAuthorAndDateRange(eq(authorId), any(DateRangeRequestDTO.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando autor não existe")
        void deveLancarExcecaoQuandoAutorNaoExiste() {
            Long authorId = 999L;
            when(NewsControllerImplTest.this.newsService.findNewsByAuthorAndDateRange(anyLong(), any(DateRangeRequestDTO.class)))
                    .thenThrow(new RecursoNaoEncontradoException("Autor não encontrado"));

            assertThatThrownBy(() -> NewsControllerImplTest.this.newsController
                    .getNewsByAuthorAndDateRange(authorId, NewsControllerImplTest.this.dateRangeRequest))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessageContaining("Autor não encontrado");
        }
    }
}
