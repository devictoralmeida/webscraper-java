package br.com.devictoralmeida.webscraper.java.dtos.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para a classe AuthorNewsCountResponseDTO")
class AuthorNewsCountResponseDTOTest {
    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com valores válidos")
    void deveCriarAuthorNewsCountResponseDTOComValoresValidos() {
        Long id = 1L;
        String name = "João Silva";
        Long newsCount = 10L;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(id, name, newsCount);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNewsCount()).isEqualTo(newsCount);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com id nulo")
    void deveCriarAuthorNewsCountResponseDTOComIdNulo() {
        String name = "Maria Santos";
        Long newsCount = 5L;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(null, name, newsCount);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNewsCount()).isEqualTo(newsCount);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com nome nulo")
    void deveCriarAuthorNewsCountResponseDTOComNomeNulo() {
        Long id = 2L;
        Long newsCount = 7L;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(id, null, newsCount);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getNewsCount()).isEqualTo(newsCount);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com quantidade zero")
    void deveCriarAuthorNewsCountResponseDTOComQuantidadeZero() {
        Long id = 3L;
        String name = "Pedro Alves";

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(id, name, 0L);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNewsCount()).isZero();
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com nome vazio")
    void deveCriarAuthorNewsCountResponseDTOComNomeVazio() {
        Long id = 4L;
        Long newsCount = 3L;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(id, "", newsCount);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEmpty();
        assertThat(dto.getNewsCount()).isEqualTo(newsCount);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com valores máximos de Long")
    void deveCriarAuthorNewsCountResponseDTOComValoresMaximos() {
        Long idMaximo = Long.MAX_VALUE;
        String name = "Teste Máximo";
        Long newsCountMaximo = Long.MAX_VALUE;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(idMaximo, name, newsCountMaximo);

        assertThat(dto.getId()).isEqualTo(idMaximo);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNewsCount()).isEqualTo(newsCountMaximo);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com todos os valores nulos")
    void deveCriarAuthorNewsCountResponseDTOComTodosValoresNulos() {
        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(null, null, null);

        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getNewsCount()).isNull();
    }

    @Test
    @DisplayName("Deve verificar que o DTO implementa Serializable")
    void deveVerificarQueODTOImplementaSerializable() {
        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(1L, "Autor Teste", 5L);

        assertThat(dto).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve criar um AuthorNewsCountResponseDTO com newsCount negativo")
    void deveCriarAuthorNewsCountResponseDTOComNewsCountNegativo() {
        Long id = 5L;
        String name = "Carlos Lima";
        Long newsCount = -1L;

        AuthorNewsCountResponseDTO dto = new AuthorNewsCountResponseDTO(id, name, newsCount);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getNewsCount()).isNegative();
    }
}
