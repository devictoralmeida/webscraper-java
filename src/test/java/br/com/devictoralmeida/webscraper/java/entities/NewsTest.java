package br.com.devictoralmeida.webscraper.java.entities;

import br.com.devictoralmeida.webscraper.java.dtos.ParsedNewsDTO;
import br.com.devictoralmeida.webscraper.java.dtos.PartialNewsDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para a classe News")
class NewsTest {

    @Test
    @DisplayName("Deve criar News a partir de ParsedNewsDTO e Author mapeando todos os campos")
    void deveCriarNewsAPartirDeParsedNewsDTOEAuthor() {
        PartialNewsDTO partial = new PartialNewsDTO("https://example.com/noticia", "Título");
        LocalDateTime publishDate = LocalDateTime.of(2024, 1, 15, 10, 30);
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                "Subtítulo",
                "Conteúdo da notícia",
                publishDate,
                "Nome do Autor no DTO"
        );
        Author author = new Author("João Silva");

        News news = new News(parsed, author);

        assertThat(news.getId()).isNull();
        assertThat(news.getUrl()).isEqualTo("https://example.com/noticia");
        assertThat(news.getTitle()).isEqualTo("Título");
        assertThat(news.getSubtitle()).isEqualTo("Subtítulo");
        assertThat(news.getContent()).isEqualTo("Conteúdo da notícia");
        assertThat(news.getPublishDate()).isEqualTo(publishDate);
        assertThat(news.getAuthor()).isEqualTo(author);
        assertThat(news.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve ignorar authorName do ParsedNewsDTO e usar o Author passado ao construtor")
    void deveIgnorarAuthorNameDoDTOEUsarAuthorDoConstrutor() {
        PartialNewsDTO partial = new PartialNewsDTO("https://site.com/p", "T");
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                null,
                "C",
                LocalDateTime.now(),
                "Autor do DTO deve ser ignorado"
        );
        Author author = new Author("Autor Válido");

        News news = new News(parsed, author);

        assertThat(news.getAuthor()).isNotNull();
        assertThat(news.getAuthor().getName()).isEqualTo("Autor Válido");
    }

    @Test
    @DisplayName("Deve permitir subtitle nulo ao criar via ParsedNewsDTO")
    void devePermitirSubtitleNuloAoCriarViaDTO() {
        PartialNewsDTO partial = new PartialNewsDTO("https://x.com/n", "Título Base");
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                null,
                "Conteúdo",
                LocalDateTime.of(2024, 2, 1, 8, 0),
                "Autor DTO"
        );

        News news = new News(parsed, new Author("Maria"));

        assertThat(news.getSubtitle()).isNull();
        assertThat(news.getTitle()).isEqualTo("Título Base");
    }

    @Test
    @DisplayName("Deve permitir Author nulo ao criar via ParsedNewsDTO")
    void devePermitirAuthorNuloAoCriarViaDTO() {
        PartialNewsDTO partial = new PartialNewsDTO("https://y.com/a", "Titulo");
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                "Sub",
                "Conteúdo",
                LocalDateTime.of(2024, 3, 10, 12, 0),
                "Autor DTO"
        );

        News news = new News(parsed, null);

        assertThat(news.getAuthor()).isNull();
    }

    @Test
    @DisplayName("Deve criar News usando construtor sem argumentos com createdAt inicializado")
    void deveCriarNewsSemArgsComCreatedAtInicializado() {
        News news = new News();

        assertThat(news.getId()).isNull();
        assertThat(news.getUrl()).isNull();
        assertThat(news.getTitle()).isNull();
        assertThat(news.getSubtitle()).isNull();
        assertThat(news.getContent()).isNull();
        assertThat(news.getAuthor()).isNull();
        assertThat(news.getPublishDate()).isNull();
        assertThat(news.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve permitir modificar campos via setters")
    void devePermitirModificarCamposViaSetters() {
        News news = new News();

        LocalDateTime publishAt = LocalDateTime.of(2024, 4, 5, 9, 30);
        LocalDateTime createdAt = LocalDateTime.of(2024, 4, 5, 9, 45);
        Author author = new Author("Autor X");

        news.setId(1L);
        news.setUrl("https://example.com/novo");
        news.setTitle("Novo Título");
        news.setSubtitle("Novo Subtítulo");
        news.setContent("Novo Conteúdo");
        news.setAuthor(author);
        news.setPublishDate(publishAt);
        news.setCreatedAt(createdAt);

        assertThat(news.getId()).isEqualTo(1L);
        assertThat(news.getUrl()).isEqualTo("https://example.com/novo");
        assertThat(news.getTitle()).isEqualTo("Novo Título");
        assertThat(news.getSubtitle()).isEqualTo("Novo Subtítulo");
        assertThat(news.getContent()).isEqualTo("Novo Conteúdo");
        assertThat(news.getAuthor()).isEqualTo(author);
        assertThat(news.getPublishDate()).isEqualTo(publishAt);
        assertThat(news.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("Deve suportar conteúdo longo")
    void deveSuportarConteudoLongo() {
        String conteudoLongo = "A".repeat(10_000);
        PartialNewsDTO partial = new PartialNewsDTO("https://example.com/longo", "Título Longo");
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                "Sub",
                conteudoLongo,
                LocalDateTime.now(),
                "Autor DTO"
        );

        News news = new News(parsed, new Author("Autor Longo"));

        assertThat(news.getContent()).hasSize(10_000).isEqualTo(conteudoLongo);
    }

    @Test
    @DisplayName("Deve preservar caracteres especiais nos campos de texto")
    void devePreservarCaracteresEspeciais() {
        PartialNewsDTO partial = new PartialNewsDTO(
                "https://example.com/notícia-àçéñtüädã",
                "Título com Çédilha"
        );
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                "Subtítulo com €$£¥",
                "Conteúdo com <html> & \"aspas\"",
                LocalDateTime.now(),
                "José María"
        );

        News news = new News(parsed, new Author("José María"));

        assertThat(news.getUrl()).contains("notícia-àçéñtüädã");
        assertThat(news.getTitle()).contains("Çédilha");
        assertThat(news.getSubtitle()).contains("€$£¥");
        assertThat(news.getContent()).contains("<html>").contains("\"aspas\"");
    }

    @Test
    @DisplayName("Deve verificar que a classe implementa Serializable")
    void deveVerificarQueImplementaSerializable() {
        assertThat(new News()).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve mapear corretamente o publishDate vindo do DTO")
    void deveMapearCorretamentePublishDate() {
        LocalDateTime expected = LocalDateTime.of(2024, 7, 1, 13, 15);
        PartialNewsDTO partial = new PartialNewsDTO("https://example.com/pub", "Pub");
        ParsedNewsDTO parsed = new ParsedNewsDTO(
                partial,
                null,
                "Content",
                expected,
                "Autor DTO"
        );

        News news = new News(parsed, new Author("Autor"));

        assertThat(news.getPublishDate()).isEqualTo(expected);
    }
}
