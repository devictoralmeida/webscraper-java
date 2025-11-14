package br.com.devictoralmeida.webscraper.java.exception;

import java.io.Serial;

public class ProibidoException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8803308046217866527L;

    public ProibidoException(String message) {
        super(message);
    }
}
