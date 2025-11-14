package br.com.devictoralmeida.webscraper.java.dtos.request;

import br.com.devictoralmeida.webscraper.java.shared.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DateRangeRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5990707058279563448L;

    @Schema(type = "string", example = "14/11/2025", pattern = Constants.BR_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.BR_DATE_PATTERN)
    @PastOrPresent(message = Constants.DATA_INICIO_INVALIDA)
    private LocalDateTime inicio;

    @Schema(type = "string", example = "14/11/2025", pattern = Constants.BR_DATE_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.BR_DATE_PATTERN)
    @PastOrPresent(message = Constants.DATA_FIM_INVALIDA)
    private LocalDateTime fim;
}
