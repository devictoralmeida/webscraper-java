package br.com.devictoralmeida.webscraper.java.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "News", description = "Endpoints para gerenciar o scrapping notícias")
public interface NewsController {
    @Operation(summary = "Contrato de rota para buscar e salvar notícias baseado nos parâmetros de requisição", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso.")
    })
    ResponseEntity<?> fetchNews(int pageLimit);
}
