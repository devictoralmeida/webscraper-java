package br.com.devictoralmeida.webscraper.java.dtos;

import br.com.devictoralmeida.webscraper.java.entities.Author;
import br.com.devictoralmeida.webscraper.java.shared.utils.DateUtils;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public final class AuthorResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2670551977462063258L;

    private final Long id;

    private final String name;

    private final String createdAt;

    public AuthorResponseDTO(Author author) {
        this.id = author.getId();
        this.name = author.getName();
        this.createdAt = DateUtils.format(author.getCreatedAt());
    }
}
