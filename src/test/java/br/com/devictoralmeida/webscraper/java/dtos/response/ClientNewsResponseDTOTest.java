package br.com.devictoralmeida.webscraper.java.dtos.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests para a classe ClientNewsResponseDTO")
class ClientNewsResponseDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com todos os valores válidos")
    void deveCriarClientNewsResponseDTOComTodosValoresValidos() {
        String postTitle = "Título da Notícia";
        String link = "https://example.com/noticia";

        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(postTitle, link);

        assertThat(dto.getPostTitle()).isEqualTo(postTitle);
        assertThat(dto.getLink()).isEqualTo(link);
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO usando construtor sem argumentos")
    void deveCriarClientNewsResponseDTOUsandoConstrutorSemArgumentos() {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO();

        assertThat(dto.getPostTitle()).isNull();
        assertThat(dto.getLink()).isNull();
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com postTitle nulo")
    void deveCriarClientNewsResponseDTOComPostTitleNulo() {
        String link = "https://example.com/noticia";

        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(null, link);

        assertThat(dto.getPostTitle()).isNull();
        assertThat(dto.getLink()).isEqualTo(link);
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com link nulo")
    void deveCriarClientNewsResponseDTOComLinkNulo() {
        String postTitle = "Título da Notícia";

        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(postTitle, null);

        assertThat(dto.getPostTitle()).isEqualTo(postTitle);
        assertThat(dto.getLink()).isNull();
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com todos os valores nulos")
    void deveCriarClientNewsResponseDTOComTodosValoresNulos() {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(null, null);

        assertThat(dto.getPostTitle()).isNull();
        assertThat(dto.getLink()).isNull();
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com strings vazias")
    void deveCriarClientNewsResponseDTOComStringsVazias() {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO("", "");

        assertThat(dto.getPostTitle()).isEmpty();
        assertThat(dto.getLink()).isEmpty();
    }

    @Test
    @DisplayName("Deve modificar os valores usando setters")
    void deveModificarValoresUsandoSetters() {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO();

        dto.setPostTitle("Novo Título");
        dto.setLink("https://example.com/novo-link");

        assertThat(dto.getPostTitle()).isEqualTo("Novo Título");
        assertThat(dto.getLink()).isEqualTo("https://example.com/novo-link");
    }

    @Test
    @DisplayName("Deve deserializar JSON corretamente usando @JsonProperty")
    void deveDDeserializarJsonCorretamenteUsandoJsonProperty() throws Exception {
        String json = """
                {
                    "post_title": "Título da API",
                    "post_permalink": "https://api.example.com/noticia"
                }
                """;

        ClientNewsResponseDTO dto = this.objectMapper.readValue(json, ClientNewsResponseDTO.class);

        assertThat(dto.getPostTitle()).isEqualTo("Título da API");
        assertThat(dto.getLink()).isEqualTo("https://api.example.com/noticia");
    }

    @Test
    @DisplayName("Deve serializar para JSON corretamente usando @JsonProperty")
    void deveSerializarParaJsonCorretamenteUsandoJsonProperty() throws Exception {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO("Título Teste", "https://test.com/link");

        String json = this.objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"post_title\":\"Título Teste\"");
        assertThat(json).contains("\"post_permalink\":\"https://test.com/link\"");
    }

    @Test
    @DisplayName("Deve ignorar propriedades desconhecidas no JSON usando @JsonIgnoreProperties")
    void deveIgnorarPropriedadesDesconhecidasNoJsonUsandoJsonIgnoreProperties() throws Exception {
        String json = """
                {
                    "post_title": "Título",
                    "post_permalink": "https://example.com",
                    "propriedade_desconhecida": "valor",
                    "outra_propriedade": 123
                }
                """;

        ClientNewsResponseDTO dto = this.objectMapper.readValue(json, ClientNewsResponseDTO.class);

        assertThat(dto.getPostTitle()).isEqualTo("Título");
        assertThat(dto.getLink()).isEqualTo("https://example.com");
    }

    @Test
    @DisplayName("Deve verificar que o DTO implementa Serializable")
    void deveVerificarQueODTOImplementaSerializable() {
        ClientNewsResponseDTO dto = new ClientNewsResponseDTO("Teste", "https://test.com");

        assertThat(dto).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO preservando caracteres especiais")
    void deveCriarClientNewsResponseDTOPreservandoCaracteresEspeciais() {
        String postTitle = "Título com Çédilha e Àcentuação €$£¥";
        String link = "https://example.com/notícia-àçéñtüädã";

        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(postTitle, link);

        assertThat(dto.getPostTitle()).isEqualTo(postTitle);
        assertThat(dto.getLink()).isEqualTo(link);
    }

    @Test
    @DisplayName("Deve criar um ClientNewsResponseDTO com postTitle longo")
    void deveCriarClientNewsResponseDTOComPostTitleLongo() {
        String postTitleLongo = "A".repeat(1000);
        String link = "https://example.com/noticia";

        ClientNewsResponseDTO dto = new ClientNewsResponseDTO(postTitleLongo, link);

        assertThat(dto.getPostTitle()).hasSize(1000);
        assertThat(dto.getLink()).isEqualTo(link);
    }

    @Test
    @DisplayName("Deve deserializar JSON com valores nulos")
    void deveDDeserializarJsonComValoresNulos() throws Exception {
        String json = """
                {
                    "post_title": null,
                    "post_permalink": null
                }
                """;

        ClientNewsResponseDTO dto = this.objectMapper.readValue(json, ClientNewsResponseDTO.class);

        assertThat(dto.getPostTitle()).isNull();
        assertThat(dto.getLink()).isNull();
    }

    @Test
    @DisplayName("Deve deserializar JSON com campos ausentes")
    void deveDDeserializarJsonComCamposAusentes() throws Exception {
        String json = "{}";

        ClientNewsResponseDTO dto = this.objectMapper.readValue(json, ClientNewsResponseDTO.class);

        assertThat(dto.getPostTitle()).isNull();
        assertThat(dto.getLink()).isNull();
    }
}
