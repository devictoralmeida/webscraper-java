package br.com.devictoralmeida.webscraper.java.controllers;

import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notícias", description = "Endpoints para buscar e consultar notícias")
public interface NewsController {
    @Operation(summary = "Dispara o processo de scraping para buscar e salvar notícias.", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso.")
    })
    @GetMapping("/buscar")
    ResponseEntity<?> fetchNews(@RequestParam(name = "limit", defaultValue = "15") int pageLimit);

    @Operation(summary = "Busca os autores mais ativos em um período.", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso.")
    })
    @PostMapping("/relatorios/autores")
    ResponseEntity<?> getTopAuthorsByDateRange(@Valid @RequestBody DateRangeRequestDTO dto);

    @Operation(summary = "Busca notícias de um autor específico em um período.", responses = {
            @ApiResponse(responseCode = "200", description = "Sucesso.")
    })
    @PostMapping("/relatorios/autor/{authorId}")
    ResponseEntity<?> getNewsByAuthorAndDateRange(
            @PathVariable Long authorId,
            @Valid @RequestBody DateRangeRequestDTO dto
    );
}
