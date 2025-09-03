package com.example.weather;

import com.example.weather.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEntityNotFound() throws Exception {
        EntityNotFoundException ex = new EntityNotFoundException("not found");

        ErrorResponse response = handler.handleEntityNotFound(ex);

        assertEquals("not found", response.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.name(), response.getErrorCode());

        ResponseStatus status = GlobalExceptionHandler.class
                .getMethod("handleEntityNotFound", EntityNotFoundException.class)
                .getAnnotation(ResponseStatus.class);
        assertNotNull(status);
        assertEquals(HttpStatus.NOT_FOUND, status.value());
    }

    @Test
    void handleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");

        ResponseEntity<ErrorResponse> entity = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals("bad arg", entity.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.name(), entity.getBody().getErrorCode());
    }

    @Test
    void handleValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "email", "must not be blank"));
        bindingResult.addError(new FieldError("obj", "city", "must not be blank"));

        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("dummy", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ErrorResponse response = handler.handleValidationErrors(ex);

        assertEquals("email: must not be blank, city: must not be blank", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.name(), response.getErrorCode());

        ResponseStatus status = GlobalExceptionHandler.class
                .getMethod("handleValidationErrors", MethodArgumentNotValidException.class)
                .getAnnotation(ResponseStatus.class);
        assertNotNull(status);
        assertEquals(HttpStatus.BAD_REQUEST, status.value());
    }

    @SuppressWarnings("unused")
    private void dummy(String arg) {
        // no-op
    }
}

