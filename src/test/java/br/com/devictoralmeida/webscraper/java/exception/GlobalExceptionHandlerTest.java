package br.com.devictoralmeida.webscraper.java.exception;

import br.com.devictoralmeida.webscraper.java.dtos.response.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
@DisplayName("Testes para a classe GlobalExceptionHandlerTest")
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve lidar com DataIntegrityViolationException e retornar status CONFLICT")
    void deveLidarComDataIntegrityViolationExceptionERetornarStatusConflict() {
        DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
        Throwable throwable = mock(Throwable.class);

        when(ex.getMostSpecificCause()).thenReturn(throwable);
        when(throwable.getMessage()).thenReturn(GlobalExceptionConstants.PSQL_ERROR_MESSAGE);

        ResponseEntity<Object> response = (ResponseEntity<Object>) this.exceptionHandler.handlePSQLException(ex, this.request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(GlobalExceptionConstants.PSQL_ERROR_MESSAGE, ((ResponseDto<?>) response.getBody()).getMensagem());
    }

    @Test
    @DisplayName("Deve lidar com MethodArgumentNotValidException e retornar status BAD_REQUEST")
    void deveLidarComMethodArgumentNotValidExceptionERetornarStatusBadRequest() {
        WebRequest request = mock(WebRequest.class);
        HttpHeaders headers = new HttpHeaders();

        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("objectName", "field1", "Erro no campo 1"),
                new FieldError("objectName", "field2", "Erro no campo 2")
        );
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Object> responseEntity = this.exceptionHandler.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, request);
        ResponseEntity<Object> expectedResponseEntity = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, "Favor verifique todos os campos com validação", Arrays.asList("Erro no campo 1", "Erro no campo 2")));
        assertEquals(expectedResponseEntity.getStatusCode(), responseEntity.getStatusCode());
    }


    @Test
    @DisplayName("Deve lidar com DateTimeParseException e retornar status BAD_REQUEST")
    void deveLidarComDateTimeParseExceptionERetornarStatusBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        HttpHeaders headers = new HttpHeaders();
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletWebRequest webRequest = new ServletWebRequest(request);

        DateTimeParseException dateTimeParseException = mock(DateTimeParseException.class);
        when(ex.getRootCause()).thenReturn(dateTimeParseException);
        when(dateTimeParseException.getParsedString()).thenReturn("InvalidDateTime");

        final String errorMessage = "Formato de data/hora inválido: InvalidDateTime. Use os formatos apropriados para data e hora.";
        when(ex.getLocalizedMessage()).thenReturn(errorMessage);

        var responseEntity = this.exceptionHandler.handleHttpMessageNotReadable(ex, headers, status, webRequest);

        var expectedResponseEntity = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, errorMessage);
        assertEquals(expectedResponseEntity.getStatus(), responseEntity.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve lidar com NegocioException e retornar status BAD_REQUEST")
    void deveLidarComNegocioException() {
        NegocioException ex = mock(NegocioException.class);
        String errorMessage = "Mensagem de erro";
        when(ex.getMessage()).thenReturn(errorMessage);

        var responseEntity = (ResponseEntity<ResponseDto>) this.exceptionHandler.handleNegocioException(ex, this.request);

        var expectedResponseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, errorMessage, Arrays.asList(errorMessage)));

        assertEquals(expectedResponseEntity.getStatusCode(), responseEntity.getStatusCode());
        assertEquals(expectedResponseEntity.getBody().getMensagem(), responseEntity.getBody().getMensagem());
    }

    @Test
    @DisplayName("Deve lidar com RecursoNaoEncontradoException e retornar status NOT_FOUND")
    void deveLidarComRecursoNaoEncontradoExceptionERetornarStatusNotFound() {
        RecursoNaoEncontradoException ex = mock(RecursoNaoEncontradoException.class);
        when(ex.getMessage()).thenReturn("Recurso não encontrado");

        ResponseEntity<Object> response = (ResponseEntity<Object>) this.exceptionHandler.handleRecursoNaoEncontradoException(ex, this.request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(GlobalExceptionConstants.MENSAGEM_RECURSO_NAO_ENCONTRADO, ((ResponseDto<?>) response.getBody()).getMensagem());
    }

    @Test
    @DisplayName("Deve lidar com SemAutorizacaoException e retornar status FORBIDDEN")
    void deveLidarComSemAutorizacaoExceptionERetornarStatusForbidden() {
        SemAutorizacaoException ex = mock(SemAutorizacaoException.class);
        when(ex.getMessage()).thenReturn("Erro de autorização");

        ResponseEntity<Object> response = (ResponseEntity<Object>) this.exceptionHandler.handleSemAutorizacaoException(ex, this.request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("É necessário estar autenticado para acessar este recurso!", ((ResponseDto<?>) response.getBody()).getMensagem());
    }

    @Test
    @DisplayName("Deve lidar com ParametrosDeConsultaInvalidosException e retornar status BAD_REQUEST")
    void deveLidarComParametrosDeConsultaInvalidosExceptionERetornarStatusBadRequest() {
        ParametrosDeConsultaInvalidosException ex = mock(ParametrosDeConsultaInvalidosException.class);
        when(ex.getMessage()).thenReturn("Parâmetros de consulta inválidos");

        ResponseEntity<Object> response = (ResponseEntity<Object>) this.exceptionHandler.handleInvalidQueryParamsException(ex, this.request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Parâmetros de consulta inválidos", ((ResponseDto<?>) response.getBody()).getMensagem());
    }

    @Test
    @DisplayName("Deve lidar com MethodArgumentTypeMismatchException e retornar status BAD_REQUEST")
    void deveLidarComMethodArgumentTypeMismatchExceptionERetornarStatusBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("param");

        ResponseEntity<Object> response = (ResponseEntity<Object>) this.exceptionHandler.handleMethodArgumentTypeMismatchException(ex, this.request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(GlobalExceptionConstants.MENSAGEM_TIPO_PARAMETRO_INVALIDO + "param", ((ResponseDto<?>) response.getBody()).getMensagem());
    }

}