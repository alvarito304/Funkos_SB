package dev.alvaroherrero.funkosb.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFoundException extends FunkoException {
    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public FunkoNotFoundException(String message) {
        super(message);
    }
    public FunkoNotFoundException(long id) {
        super(String.format("Funko con id " + id + " not found"));
    }
}
