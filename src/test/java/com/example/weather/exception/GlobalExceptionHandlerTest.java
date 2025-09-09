package com.example.weather.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
}

