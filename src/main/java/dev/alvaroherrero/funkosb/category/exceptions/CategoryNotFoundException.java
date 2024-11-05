package dev.alvaroherrero.funkosb.category.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends CategoryException{
    @Serial
    private static final long serialVersionUID = 43876691117560211L;
    public CategoryNotFoundException(UUID id) {
        super(String.format("Category with id " + id + " not found"));
    }
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
