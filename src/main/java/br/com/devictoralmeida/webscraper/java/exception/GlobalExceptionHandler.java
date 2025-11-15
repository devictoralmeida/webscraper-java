package br.com.devictoralmeida.webscraper.java.exception;

import br.com.devictoralmeida.webscraper.java.dtos.response.ResponseDto;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handlePSQLException(DataIntegrityViolationException ex, WebRequest request) {
        this.logger.error(" =========== DataIntegrityViolationException =========== " + ex.getMostSpecificCause().getMessage());
        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.CONFLICT, GlobalExceptionConstants.PSQL_ERROR_MESSAGE, Collections.singletonList(ex.getMostSpecificCause().getMessage()));
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleWithResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        this.logger.error(" =============== MissingServletRequestParameterException ==========================");
        String field = ex.getParameterName();
        String error = GlobalExceptionConstants.MENSAGEM_PARAMETRO_OBRIGATORIO_NAO_INFORMADO + ex.getParameterName();
        ResponseDto<Object> response = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, error, Arrays.asList(field));
        return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        this.logger.error(" =============== DTO fields that failed validation ==========================");

        List<String> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        ResponseDto<Object> res = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST,
                GlobalExceptionConstants.CHECK_FIELDS_MESSAGE, erros);

        return handleExceptionInternal(ex, res, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof InvalidFormatException) {
            this.logger.error(" =============== HttpMessageNotReadableException validation ENUM ==========================");

            InvalidFormatException invalidFormatException = (InvalidFormatException) rootCause;
            String invalidValue = invalidFormatException.getValue().toString();
            String enumType = invalidFormatException.getTargetType().getSimpleName();
            List<String> enumValues = Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .toList();

            String errorMessage = String.format(
                    GlobalExceptionConstants.MENSAGEM_VALOR_INVALIDO,
                    enumType, invalidValue, enumValues);

            ResponseDto<Object> res = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, errorMessage);
            return handleExceptionInternal(ex, res, headers, HttpStatus.BAD_REQUEST, request);
        } else if (rootCause instanceof DateTimeParseException) {
            this.logger.error(" =============== HttpMessageNotReadableException validation LocalDate or LocalDateTime ==========================");

            DateTimeParseException dateTimeParseException = (DateTimeParseException) rootCause;
            String invalidValue = dateTimeParseException.getParsedString();
            String errorMessage = GlobalExceptionConstants.MENSAGEM_FORMATO_DATA_INVALIDO + invalidValue + ". ";

            if (ex.getLocalizedMessage().contains("LocalDate")) {
                errorMessage += GlobalExceptionConstants.MENSAGEM_FORMATO_ESPERADO_DATA;
            } else {
                errorMessage += GlobalExceptionConstants.MENSAGEM_USAR_FORMATOS_APROPRIADOS;
            }

            ResponseDto<Object> res = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, errorMessage);
            return handleExceptionInternal(ex, res, headers, HttpStatus.BAD_REQUEST, request);
        }

        return handleExceptionInternal(ex, null, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({NegocioException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleNegocioException(NegocioException ex, WebRequest request) {
        this.logger.error(" =============== NegocioException ==========================");

        String field = ex.getMessage();
        String error = ex.getMessage();

        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, error, Arrays.asList(field));
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ParametrosDeConsultaInvalidosException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleInvalidQueryParamsException(ParametrosDeConsultaInvalidosException ex, WebRequest request) {
        this.logger.error(" =============== InvalidQueryParamsException ==========================");

        String error = ex.getMessage();

        ResponseDto<Object> obj = ResponseDto.fromData(
                null,
                HttpStatus.BAD_REQUEST,
                error,
                Arrays.asList(GlobalExceptionConstants.MENSAGEM_PARAMETROS_CONSULTA_INVALIDOS)
        );
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        this.logger.error(" =============== MethodArgumentTypeMismatchException ==========================");

        String error = GlobalExceptionConstants.MENSAGEM_TIPO_PARAMETRO_INVALIDO + ex.getName();

        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, error);

        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({SemAutorizacaoException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleSemAutorizacaoException(SemAutorizacaoException ex, WebRequest request) {
        this.logger.error(" =============== SemAutorizacaoException ==========================");

        String error = GlobalExceptionConstants.MENSAGEM_SEM_AUTORIZACAO;
        String field = ex.getMessage();

        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.UNAUTHORIZED, error, Arrays.asList(field));
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({ProibidoException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> handleProibidoException(ProibidoException ex, WebRequest request) {
        this.logger.error(" =============== ProibidoException ==========================");

        String error = GlobalExceptionConstants.MENSAGEM_PROIBIDO;
        String field = ex.getMessage();

        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.FORBIDDEN, error, Arrays.asList(field));
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({RecursoNaoEncontradoException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex, WebRequest request) {
        this.logger.error(" =============== RecursoNaoEncontradoException ==========================");

        String error = GlobalExceptionConstants.MENSAGEM_RECURSO_NAO_ENCONTRADO;
        String field = ex.getMessage();

        ResponseDto<Object> obj = ResponseDto.fromData(null, HttpStatus.NOT_FOUND, error, Arrays.asList(field));
        return handleExceptionInternal(ex, obj, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}