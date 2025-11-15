package br.com.devictoralmeida.webscraper.java.shared.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Testes para DateUtils")
class DateUtilsTest {
    @Nested
    @DisplayName("Testes para format")
    class FormatTests {

        @Test
        @DisplayName("Deve formatar LocalDateTime para string no padrão dd/MM/yyyy HH:mm")
        void deveFormatarLocalDateTimeParaString() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 14, 30);

            String result = DateUtils.format(dateTime);

            assertThat(result).isEqualTo("15/06/2024 14:30");
        }

        @Test
        @DisplayName("Deve retornar null quando LocalDateTime for null")
        void deveRetornarNullQuandoLocalDateTimeForNull() {
            String result = DateUtils.format(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Deve formatar corretamente com zeros à esquerda")
        void deveFormatarCorretamenteComZerosAEsquerda() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 1, 5, 8, 5);

            String result = DateUtils.format(dateTime);

            assertThat(result).isEqualTo("05/01/2024 08:05");
        }

        @Test
        @DisplayName("Deve formatar corretamente meia-noite")
        void deveFormatarCorretamenteMeiaNoite() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 12, 31, 0, 0);

            String result = DateUtils.format(dateTime);

            assertThat(result).isEqualTo("31/12/2024 00:00");
        }

        @Test
        @DisplayName("Deve formatar corretamente fim do dia")
        void deveFormatarCorretamenteFimDoDia() {
            LocalDateTime dateTime = LocalDateTime.of(2024, 12, 31, 23, 59);

            String result = DateUtils.format(dateTime);

            assertThat(result).isEqualTo("31/12/2024 23:59");
        }
    }

    @Nested
    @DisplayName("Testes para parse")
    class ParseTests {

        @Test
        @DisplayName("Deve fazer parse de string ISO para LocalDateTime")
        void deveFazerParseDeStringISOParaLocalDateTime() {
            String dateTimeStr = "2024-06-15T14:30:00-03:00";

            LocalDateTime result = DateUtils.parse(dateTimeStr);

            assertThat(result).isNotNull();
            assertThat(result.getYear()).isEqualTo(2024);
            assertThat(result.getMonthValue()).isEqualTo(6);
            assertThat(result.getDayOfMonth()).isEqualTo(15);
            assertThat(result.getHour()).isEqualTo(14);
            assertThat(result.getMinute()).isEqualTo(30);
        }

        @Test
        @DisplayName("Deve retornar null quando string for null")
        void deveRetornarNullQuandoStringForNull() {
            LocalDateTime result = DateUtils.parse(null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Deve retornar null quando string for vazia")
        void deveRetornarNullQuandoStringForVazia() {
            LocalDateTime result = DateUtils.parse("");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Deve lançar exceção quando formato for inválido")
        void deveLancarExcecaoQuandoFormatoForInvalido() {
            String dateTimeStr = "15/06/2024 14:30";

            assertThatThrownBy(() -> DateUtils.parse(dateTimeStr))
                    .isInstanceOf(DateTimeParseException.class);
        }

        @Test
        @DisplayName("Deve fazer parse com offset positivo")
        void deveFazerParseComOffsetPositivo() {
            String dateTimeStr = "2024-06-15T14:30:00+05:30";

            LocalDateTime result = DateUtils.parse(dateTimeStr);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Deve fazer parse com offset UTC (Z)")
        void deveFazerParseComOffsetUTC() {
            String dateTimeStr = "2024-06-15T14:30:00Z";

            LocalDateTime result = DateUtils.parse(dateTimeStr);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Deve fazer parse de meia-noite")
        void deveFazerParseDeMeiaNoite() {
            String dateTimeStr = "2024-12-31T00:00:00-03:00";

            LocalDateTime result = DateUtils.parse(dateTimeStr);

            assertThat(result).isNotNull();
            assertThat(result.getHour()).isEqualTo(0);
            assertThat(result.getMinute()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Testes de integração format e parse")
    class IntegrationTests {

        @Test
        @DisplayName("Deve manter consistência ao formatar e fazer parse")
        void deveManterConsistenciaAoFormatarEFazerParse() {
            LocalDateTime original = LocalDateTime.of(2024, 6, 15, 14, 30);
            String isoString = "2024-06-15T14:30:00-03:00";

            LocalDateTime parsed = DateUtils.parse(isoString);
            String formatted = DateUtils.format(parsed);

            assertThat(formatted).isEqualTo("15/06/2024 14:30");
        }
    }

    @Nested
    @DisplayName("Testes de construtor privado")
    class ConstructorTests {

        @Test
        @DisplayName("Deve ter construtor privado")
        void deveTerConstrutorPrivado() throws Exception {
            var constructor = DateUtils.class.getDeclaredConstructor();
            assertThat(constructor.canAccess(null)).isFalse();
        }
    }
}
