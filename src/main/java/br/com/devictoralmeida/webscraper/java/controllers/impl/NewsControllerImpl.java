package br.com.devictoralmeida.webscraper.java.controllers.impl;

import br.com.devictoralmeida.webscraper.java.controllers.NewsController;
import br.com.devictoralmeida.webscraper.java.dtos.request.DateRangeRequestDTO;
import br.com.devictoralmeida.webscraper.java.dtos.response.ResponseDto;
import br.com.devictoralmeida.webscraper.java.services.NewsService;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "noticias")
@RequiredArgsConstructor
public class NewsControllerImpl implements NewsController {
    private final NewsService service;

    @Override
    @GetMapping("/buscar")
    public ResponseEntity<?> fetchNews(@RequestParam(name = "limit", defaultValue = "15") int pageLimit) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fromData(this.service.scrapeAndSaveNews(pageLimit), HttpStatus.OK, Constants.MENSAGEM_SALVO_SUCESSO));
    }

    @Override
    @PostMapping("/relatorios/autores")
    public ResponseEntity<?> getTopAuthorsByDateRange(@Valid @RequestBody DateRangeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fromData(this.service.findTopAuthorsByDateRange(dto), HttpStatus.OK, Constants.MENSAGEM_AUTORES_ENCONTRADOS_SUCESSO));
    }

    @Override
    @PostMapping("/relatorios/autor/{authorId}")
    public ResponseEntity<?> getNewsByAuthorAndDateRange(
            @PathVariable Long authorId,
            @Valid @RequestBody DateRangeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fromData(this.service.findNewsByAuthorAndDateRange(authorId, dto), HttpStatus.OK, Constants.MENSAGEM_NOTICIAS_ENCONTRADAS_SUCESSO));
    }
}
