package br.com.devictoralmeida.webscraper.java.exception;

import java.io.Serial;

public class ParametrosDeConsultaInvalidosException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8803308046217866527L;

    public ParametrosDeConsultaInvalidosException(String message) {
        super(message);
    }

    public ParametrosDeConsultaInvalidosException(String message, Throwable cause) {
        super(message, cause);
    }
}