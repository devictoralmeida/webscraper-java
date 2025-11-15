package br.com.devictoralmeida.webscraper.java.dtos.response;

import br.com.devictoralmeida.webscraper.java.entities.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para a classe AuthorResponseDTO")
class AuthorResponseDTOTest {

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com todos os valores válidos")
    void deveCriarAuthorResponseDTOComTodosValoresValidos() {
        Author author = new Author();
        author.setId(1L);
        author.setName("João Silva");
        author.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("João Silva");
        assertThat(dto.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com nome nulo")
    void deveCriarAuthorResponseDTOComNomeNulo() {
        Author author = new Author();
        author.setId(2L);
        author.setName(null);
        author.setCreatedAt(LocalDateTime.of(2024, 2, 10, 14, 0));

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com createdAt nulo")
    void deveCriarAuthorResponseDTOComCreatedAtNulo() {
        Author author = new Author();
        author.setId(3L);
        author.setName("Maria Santos");
        author.setCreatedAt(null);

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getName()).isEqualTo("Maria Santos");
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com nome vazio")
    void deveCriarAuthorResponseDTOComNomeVazio() {
        Author author = new Author();
        author.setId(4L);
        author.setName("");
        author.setCreatedAt(LocalDateTime.of(2024, 3, 5, 9, 15));

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getName()).isEmpty();
        assertThat(dto.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve formatar corretamente a data de createdAt")
    void deveFormatarCorretamenteDataCreatedAt() {
        Author author = new Author();
        author.setId(5L);
        author.setName("Pedro Alves");
        author.setCreatedAt(LocalDateTime.of(2024, 4, 20, 15, 45, 30));

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getCreatedAt()).matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}");
    }

    @Test
    @DisplayName("Deve verificar que o DTO implementa Serializable")
    void deveVerificarQueODTOImplementaSerializable() {
        Author author = new Author();
        author.setId(6L);
        author.setName("Teste Serializable");
        author.setCreatedAt(LocalDateTime.now());

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com id nulo")
    void deveCriarAuthorResponseDTOComIdNulo() {
        Author author = new Author();
        author.setId(null);
        author.setName("Carlos Lima");
        author.setCreatedAt(LocalDateTime.now());

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo("Carlos Lima");
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO preservando caracteres especiais no nome")
    void deveCriarAuthorResponseDTOPreservandoCaracteresEspeciaisNoNome() {
        Author author = new Author();
        author.setId(7L);
        author.setName("José María Gómez Çöñtrérâs");
        author.setCreatedAt(LocalDateTime.of(2024, 5, 10, 11, 30));

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getName()).isEqualTo("José María Gómez Çöñtrérâs");
        assertThat(dto.getName()).contains("José", "María", "Çöñtrérâs");
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com nome longo")
    void deveCriarAuthorResponseDTOComNomeLongo() {
        String nomeLongo = "A".repeat(500);

        Author author = new Author();
        author.setId(8L);
        author.setName(nomeLongo);
        author.setCreatedAt(LocalDateTime.now());

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getName()).hasSize(500);
        assertThat(dto.getName()).isEqualTo(nomeLongo);
    }

    @Test
    @DisplayName("Deve criar um AuthorResponseDTO com todos os valores nulos")
    void deveCriarAuthorResponseDTOComTodosValoresNulos() {
        Author author = new Author();
        author.setId(null);
        author.setName(null);
        author.setCreatedAt(null);

        AuthorResponseDTO dto = new AuthorResponseDTO(author);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
    }
}
