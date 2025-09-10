package com.example.weather.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound() {
        var ex = new NotFoundException("not found");
        var response = handler.handleNotFound(ex, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Resource not found", body.getTitle());
        assertEquals("not found", body.getDetail());
    }

    @Test
    void handleBadRequest() {
        var ex = new BadRequestException("bad request");
        var response = handler.handleBadRequest(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Bad Request", body.getTitle());
        assertEquals("bad request", body.getDetail());
    }

    @Test
    void handleOther() {
        var ex = new Exception("boom");
        var response = handler.handleOther(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Internal error", body.getTitle());
        assertEquals("Unexpected error", body.getDetail());
    }

    @Test
    void handleValidation() throws NoSuchMethodException {
        class Dummy {
            @SuppressWarnings("unused")
            void method(String arg) {
            }
        }
        var bindingResult = new BeanPropertyBindingResult(new Object(), "obj");
        bindingResult.addError(new FieldError("obj", "name", "must not be blank"));
        bindingResult.addError(new FieldError("obj", "name", "size must be 3"));
        bindingResult.addError(new FieldError("obj", "age", "must be positive"));
        var method = Dummy.class.getDeclaredMethod("method", String.class);
        var parameter = new MethodParameter(method, 0);
        var ex = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleValidation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("Bad Request", body.getTitle());
        @SuppressWarnings("unchecked")
        var errors = (Map<String, List<String>>) body.getProperties().get("errors");
        assertNotNull(errors);
        assertEquals(List.of("must not be blank", "size must be 3"), errors.get("name"));
        assertEquals(List.of("must be positive"), errors.get("age"));
    }
}

