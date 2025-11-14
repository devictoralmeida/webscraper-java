package br.com.devictoralmeida.webscraper.java.exception;

import java.io.Serial;

public class NegocioException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8803308046217866527L;

    public NegocioException(String message) {
        super(message);
    }
}
