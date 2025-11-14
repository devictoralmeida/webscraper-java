package br.com.devictoralmeida.webscraper.java.controllers.impl;

import br.com.devictoralmeida.webscraper.java.controllers.NewsController;
import br.com.devictoralmeida.webscraper.java.dtos.ResponseDto;
import br.com.devictoralmeida.webscraper.java.services.ScraperService;
import br.com.devictoralmeida.webscraper.java.shared.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "noticias")
@RequiredArgsConstructor
public class NewsControllerImpl implements NewsController {
    private final ScraperService service;

    @Override
    @GetMapping
    public ResponseEntity<?> fetchNews(@RequestParam(name = "limit", defaultValue = "15") int pageLimit) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.fromData(this.service.execute(pageLimit), HttpStatus.OK, Constants.MENSAGEM_SALVO_SUCESSO));
    }
}
