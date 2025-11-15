package br.com.devictoralmeida.webscraper.java.dtos.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResponseDtoTest {

    @Test
    @DisplayName("Deve criar um ResponseDto com dados")
    void deveCriarResponseDtoComDados() {
        String mensagem = "Usuário salvo com sucesso!";
        Integer status = 201;
        ResponseDto<Object> responseDto = ResponseDto.fromData(null, HttpStatus.CREATED, mensagem);

        assertNotNull(responseDto);
        assertEquals(mensagem, responseDto.getMensagem());
        assertEquals(status, responseDto.getStatus());
    }

    @Test
    @DisplayName("Deve criar um ResponseDto com erros")
    void deveCriarResponseDtoComErros() {
        String mensagem = "Erro ao salvar usuário!";
        Integer status = 400;
        List<String> errors = List.of("Campo 'login' é obrigatório", "Campo 'tipoPerfil' é inválido");
        ResponseDto<Object> responseDto = ResponseDto.fromData(null, HttpStatus.BAD_REQUEST, mensagem, errors);

        assertNotNull(responseDto);
        assertEquals(mensagem, responseDto.getMensagem());
        assertEquals(status, responseDto.getStatus());
        assertEquals(errors, responseDto.getErrors());
    }

    @Test
    @DisplayName("Deve retornar status e mensagem corretos para ResponseDto")
    void deveRetornarStatusEMensagemCorretos() {
        String mensagem = "Operação realizada com sucesso";
        Integer status = 200;
        ResponseDto<Object> responseDto = new ResponseDto<>();
        responseDto.setMensagem(mensagem);
        responseDto.setStatus(HttpStatus.OK);

        assertEquals(mensagem, responseDto.getMensagem());
        assertEquals(status, responseDto.getStatus());
    }

    @Test
    @DisplayName("Deve permitir adicionar e obter informações no ResponseDto")
    void devePermitirAdicionarEObterInformacoes() {
        ResponseDto<Object> responseDto = new ResponseDto<>();
        responseDto.getInfos().add("Informação adicional");

        assertNotNull(responseDto.getInfos());
        assertEquals(1, responseDto.getInfos().size());
        assertEquals("Informação adicional", responseDto.getInfos().get(0));
    }

    @Test
    @DisplayName("Deve permitir adicionar e obter warnings no ResponseDto")
    void devePermitirAdicionarEObterWarnings() {
        ResponseDto<Object> responseDto = new ResponseDto<>();
        responseDto.getWarns().add("Aviso: campo 'telefone' inválido");

        assertNotNull(responseDto.getWarns());
        assertEquals(1, responseDto.getWarns().size());
        assertEquals("Aviso: campo 'telefone' inválido", responseDto.getWarns().get(0));
    }

    public static <T> ResponseDto<T> createValidResponse(HttpStatus status, String mensagem) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.setStatus(status);
        responseDto.setMensagem(mensagem);

        responseDto.setErrors(new ArrayList<>());

        return responseDto;
    }
}
