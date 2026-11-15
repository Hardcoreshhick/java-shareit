package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleConflictException() {
        ErrorResponse response = handler.handleConflictException(new ConflictException("test"));
        assertEquals("test", response.getError());
    }

    @Test
    void handleNotFoundException() {
        ErrorResponse response = handler.handleNotFoundException(new NotFoundException("test"));
        assertEquals("test", response.getError());
    }

    @Test
    void handleValidationException() {
        ErrorResponse response = handler.handleValidationException(new ValidationException("test"));
        assertEquals("test", response.getError());
    }

    @Test
    void handleForbiddenException() {
        ErrorResponse response = handler.handleForbiddenException(new ForbiddenException("test"));
        assertEquals("test", response.getError());
    }

    @Test
    void handleException() {
        ErrorResponse response = handler.handleException(new RuntimeException("test"));
        assertNotNull(response.getError());
    }
}