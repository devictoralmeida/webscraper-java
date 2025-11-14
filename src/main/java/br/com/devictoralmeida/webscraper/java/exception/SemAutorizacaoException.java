package br.com.devictoralmeida.webscraper.java.exception;

import java.io.Serial;

public class SemAutorizacaoException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8803308046217866527L;

    public SemAutorizacaoException(String message) {
        super(message);
    }
}
