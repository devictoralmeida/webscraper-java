package br.com.devictoralmeida.webscraper.java.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@AllArgsConstructor
public final class AuthorNewsCountResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2670551977462063258L;

    private final Long id;

    private final String name;

    private final Long newsCount;
}
