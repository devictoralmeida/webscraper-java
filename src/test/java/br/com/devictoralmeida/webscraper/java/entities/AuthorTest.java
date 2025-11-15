package br.com.devictoralmeida.webscraper.java.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para a classe Author")
class AuthorTest {
    @Test
    @DisplayName("Deve modificar nome usando setter")
    void deveModificarNomeUsandoSetter() {
        Author author = new Author("Nome Original");

        author.setName("Nome Modificado");

        assertThat(author.getName()).isEqualTo("Nome Modificado");
    }

    @Test
    @DisplayName("Deve modificar id usando setter")
    void deveModificarIdUsandoSetter() {
        Author author = new Author();

        author.setId(1L);

        assertThat(author.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve modificar createdAt usando setter")
    void deveModificarCreatedAtUsandoSetter() {
        Author author = new Author();
        LocalDateTime now = LocalDateTime.now();

        author.setCreatedAt(now);

        assertThat(author.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Deve adicionar notícias à lista de newsList")
    void deveAdicionarNoticiasAListaDeNewsList() {
        Author author = new Author("Maria Santos");
        News news1 = new News();
        News news2 = new News();

        author.getNewsList().add(news1);
        author.getNewsList().add(news2);

        assertThat(author.getNewsList()).hasSize(2);
        assertThat(author.getNewsList()).contains(news1, news2);
    }

    @Test
    @DisplayName("Deve permitir modificar a lista de newsList")
    void devePermitirModificarListaDeNewsList() {
        Author author = new Author();
        ArrayList<News> newsList = new ArrayList<>();
        News news = new News();
        newsList.add(news);

        author.setNewsList(newsList);

        assertThat(author.getNewsList()).hasSize(1);
        assertThat(author.getNewsList()).contains(news);
    }

    @Test
    @DisplayName("Deve verificar que a classe implementa Serializable")
    void deveVerificarQueClasseImplementaSerializable() {
        Author author = new Author("Teste Serializable");

        assertThat(author).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve criar um Author com nome contendo caracteres especiais")
    void deveCriarAuthorComNomeContendoCaracteresEspeciais() {
        String name = "José María Gómez Çöñtrérâs";

        Author author = new Author(name);

        assertThat(author.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Deve criar um Author com nome vazio")
    void deveCriarAuthorComNomeVazio() {
        Author author = new Author("");

        assertThat(author.getName()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar um Author com nome nulo")
    void deveCriarAuthorComNomeNulo() {
        Author author = new Author(null);

        assertThat(author.getName()).isNull();
    }

    @Test
    @DisplayName("Deve permitir limpar a lista de newsList")
    void devePermitirLimparListaDeNewsList() {
        Author author = new Author();
        author.getNewsList().add(new News());
        author.getNewsList().add(new News());

        author.getNewsList().clear();

        assertThat(author.getNewsList()).isEmpty();
    }
}
