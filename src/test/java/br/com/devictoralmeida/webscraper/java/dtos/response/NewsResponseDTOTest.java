package br.com.devictoralmeida.webscraper.java.dtos.response;

import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.entities.News;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para a classe NewsResponseDTO")
class NewsResponseDTOTest {

    @Test
    @DisplayName("Deve criar um NewsResponseDTO com todos os valores válidos")
    void deveCriarNewsResponseDTOComTodosValoresValidos() {
        Author author = new Author();
        author.setId(1L);
        author.setName("João Silva");

        News news = new News();
        news.setId(1L);
        news.setUrl("https://example.com/noticia");
        news.setTitle("Título da Notícia");
        news.setSubtitle("Subtítulo da Notícia");
        news.setAuthor(author);
        news.setContent("Conteúdo completo da notícia");
        news.setPublishDate(LocalDateTime.of(2024, 1, 15, 10, 30));
        news.setCreatedAt(LocalDateTime.of(2024, 1, 15, 11, 0));

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUrl()).isEqualTo("https://example.com/noticia");
        assertThat(dto.getTitle()).isEqualTo("Título da Notícia");
        assertThat(dto.getSubtitle()).isEqualTo("Subtítulo da Notícia");
        assertThat(dto.getAuthor()).isNotNull();
        assertThat(dto.getAuthor().getId()).isEqualTo(1L);
        assertThat(dto.getAuthor().getName()).isEqualTo("João Silva");
        assertThat(dto.getContent()).isEqualTo("Conteúdo completo da notícia");
        assertThat(dto.getPublishDate()).isNotNull();
        assertThat(dto.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar um NewsResponseDTO com autor nulo")
    void deveCriarNewsResponseDTOComAutorNulo() {
        News news = new News();
        news.setId(2L);
        news.setUrl("https://example.com/noticia2");
        news.setTitle("Notícia sem Autor");
        news.setSubtitle("Subtítulo");
        news.setAuthor(null);
        news.setContent("Conteúdo da notícia");
        news.setPublishDate(LocalDateTime.of(2024, 2, 10, 14, 0));
        news.setCreatedAt(LocalDateTime.of(2024, 2, 10, 14, 30));

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getAuthor()).isNull();
        assertThat(dto.getUrl()).isEqualTo("https://example.com/noticia2");
        assertThat(dto.getTitle()).isEqualTo("Notícia sem Autor");
    }

    @Test
    @DisplayName("Deve criar um NewsResponseDTO com valores nulos permitidos")
    void deveCriarNewsResponseDTOComValoresNulosPermitidos() {
        News news = new News();
        news.setId(3L);
        news.setUrl(null);
        news.setTitle(null);
        news.setSubtitle(null);
        news.setAuthor(null);
        news.setContent(null);
        news.setPublishDate(null);
        news.setCreatedAt(null);

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getUrl()).isNull();
        assertThat(dto.getTitle()).isNull();
        assertThat(dto.getSubtitle()).isNull();
        assertThat(dto.getAuthor()).isNull();
        assertThat(dto.getContent()).isNull();
        assertThat(dto.getPublishDate()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("Deve criar um NewsResponseDTO com strings vazias")
    void deveCriarNewsResponseDTOComStringsVazias() {
        News news = new News();
        news.setId(4L);
        news.setUrl("");
        news.setTitle("");
        news.setSubtitle("");
        news.setContent("");
        news.setPublishDate(LocalDateTime.now());
        news.setCreatedAt(LocalDateTime.now());

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getUrl()).isEmpty();
        assertThat(dto.getTitle()).isEmpty();
        assertThat(dto.getSubtitle()).isEmpty();
        assertThat(dto.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Deve formatar corretamente as datas de publishDate e createdAt")
    void deveFormatarCorretamenteDatas() {
        News news = new News();
        news.setId(5L);
        news.setUrl("https://example.com/noticia5");
        news.setTitle("Notícia com Datas");
        news.setPublishDate(LocalDateTime.of(2024, 3, 20, 15, 45, 30));
        news.setCreatedAt(LocalDateTime.of(2024, 3, 20, 16, 0, 0));

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getPublishDate()).matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}");
        assertThat(dto.getCreatedAt()).matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}");
    }

    @Test
    @DisplayName("Deve verificar que o DTO implementa Serializable")
    void deveVerificarQueODTOImplementaSerializable() {
        News news = new News();
        news.setId(6L);
        news.setTitle("Teste Serializable");

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve criar um NewsResponseDTO com conteúdo longo")
    void deveCriarNewsResponseDTOComConteudoLongo() {
        String conteudoLongo = "A".repeat(10000);

        News news = new News();
        news.setId(7L);
        news.setUrl("https://example.com/noticia-longa");
        news.setTitle("Notícia Longa");
        news.setContent(conteudoLongo);
        news.setPublishDate(LocalDateTime.now());
        news.setCreatedAt(LocalDateTime.now());

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getContent()).hasSize(10000);
        assertThat(dto.getContent()).isEqualTo(conteudoLongo);
    }

    @Test
    @DisplayName("Deve criar um NewsResponseDTO preservando caracteres especiais")
    void deveCriarNewsResponseDTOPreservandoCaracteresEspeciais() {
        News news = new News();
        news.setId(8L);
        news.setUrl("https://example.com/notícia-àçéñtüädã");
        news.setTitle("Título com Çédilha e Àcentuação");
        news.setSubtitle("Subtítulo com €$£¥");
        news.setContent("Conteúdo com <html> & \"aspas\"");
        news.setPublishDate(LocalDateTime.now());
        news.setCreatedAt(LocalDateTime.now());

        NewsResponseDTO dto = new NewsResponseDTO(news);

        assertThat(dto.getUrl()).contains("notícia-àçéñtüädã");
        assertThat(dto.getTitle()).contains("Çédilha");
        assertThat(dto.getSubtitle()).contains("€$£¥");
        assertThat(dto.getContent()).contains("<html>");
    }
}
