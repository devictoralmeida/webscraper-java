package br.com.devictoralmeida.webscraper.java.dtos.request;

import br.com.devictoralmeida.webscraper.java.shared.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes para DateRangeRequestDTO")
class DateRangeRequestDTOTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Testes de validação")
    class ValidationTests {

        @Test
        @DisplayName("Deve criar DTO válido com datas corretas")
        void deveCriarDtoValidoComDatasCorretas() {
            LocalDateTime inicio = LocalDateTime.of(2024, 1, 1, 0, 0);
            LocalDateTime fim = LocalDateTime.of(2024, 12, 31, 23, 59);

            DateRangeRequestDTO dto = new DateRangeRequestDTO(inicio, fim);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).isEmpty();
            assertThat(dto.getInicio()).isEqualTo(inicio);
            assertThat(dto.getFim()).isEqualTo(fim);
        }

        @Test
        @DisplayName("Deve falhar quando data início é nula")
        void deveFalharQuandoDataInicioENula() {
            DateRangeRequestDTO dto = new DateRangeRequestDTO(null, LocalDateTime.now());

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo(Constants.DATA_INICIO_INVALIDA);
        }

        @Test
        @DisplayName("Deve falhar quando data fim é nula")
        void deveFalharQuandoDataFimENula() {
            DateRangeRequestDTO dto = new DateRangeRequestDTO(LocalDateTime.now(), null);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo(Constants.DATA_FIM_INVALIDA);
        }

        @Test
        @DisplayName("Deve falhar quando ambas as datas são nulas")
        void deveFalharQuandoAmbasAsDatasSaoNulas() {
            DateRangeRequestDTO dto = new DateRangeRequestDTO(null, null);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).hasSize(2);
        }

        @Test
        @DisplayName("Deve falhar quando data início é futura")
        void deveFalharQuandoDataInicioEFutura() {
            LocalDateTime futuro = LocalDateTime.now().plusDays(1);
            DateRangeRequestDTO dto = new DateRangeRequestDTO(futuro, LocalDateTime.now());

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo(Constants.DATA_INICIO_INVALIDA);
        }

        @Test
        @DisplayName("Deve falhar quando data fim é futura")
        void deveFalharQuandoDataFimEFutura() {
            LocalDateTime futuro = LocalDateTime.now().plusDays(1);
            DateRangeRequestDTO dto = new DateRangeRequestDTO(LocalDateTime.now(), futuro);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo(Constants.DATA_FIM_INVALIDA);
        }

        @Test
        @DisplayName("Deve aceitar data início igual a data atual")
        void deveAceitarDataInicioIgualAAtual() {
            LocalDateTime agora = LocalDateTime.now();
            DateRangeRequestDTO dto = new DateRangeRequestDTO(agora, agora);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Deve aceitar datas no passado")
        void deveAceitarDatasNoPassado() {
            LocalDateTime passado1 = LocalDateTime.now().minusMonths(6);
            LocalDateTime passado2 = LocalDateTime.now().minusMonths(1);

            DateRangeRequestDTO dto = new DateRangeRequestDTO(passado1, passado2);

            Set<ConstraintViolation<DateRangeRequestDTO>> violations = DateRangeRequestDTOTest.this.validator.validate(dto);

            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de construção e getters")
    class ConstructorAndGettersTests {

        @Test
        @DisplayName("Deve criar DTO usando construtor com parâmetros")
        void deveCriarDtoUsandoConstrutorComParametros() {
            LocalDateTime inicio = LocalDateTime.of(2024, 6, 1, 10, 30);
            LocalDateTime fim = LocalDateTime.of(2024, 6, 30, 18, 45);

            DateRangeRequestDTO dto = new DateRangeRequestDTO(inicio, fim);

            assertThat(dto.getInicio()).isEqualTo(inicio);
            assertThat(dto.getFim()).isEqualTo(fim);
        }

        @Test
        @DisplayName("Deve criar DTO usando construtor sem parâmetros")
        void deveCriarDtoUsandoConstrutorSemParametros() {
            DateRangeRequestDTO dto = new DateRangeRequestDTO();

            assertThat(dto.getInicio()).isNull();
            assertThat(dto.getFim()).isNull();
        }

        @Test
        @DisplayName("Deve manter referências corretas das datas")
        void deveManterReferenciasCorretasDasDatas() {
            LocalDateTime inicio = LocalDateTime.of(2024, 1, 15, 8, 0);
            LocalDateTime fim = LocalDateTime.of(2024, 1, 15, 20, 0);

            DateRangeRequestDTO dto = new DateRangeRequestDTO(inicio, fim);

            assertThat(dto.getInicio()).isSameAs(inicio);
            assertThat(dto.getFim()).isSameAs(fim);
        }
    }

    @Nested
    @DisplayName("Testes de serialização")
    class SerializationTests {

        @Test
        @DisplayName("Deve ser serializável")
        void deveSerSerializavel() {
            DateRangeRequestDTO dto = new DateRangeRequestDTO(
                    LocalDateTime.of(2024, 1, 1, 0, 0),
                    LocalDateTime.of(2024, 12, 31, 23, 59)
            );

            assertThat(dto).isInstanceOf(java.io.Serializable.class);
        }
    }
}
