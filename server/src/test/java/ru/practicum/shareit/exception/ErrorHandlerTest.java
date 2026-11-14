package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void handleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        ObjectError error = new ObjectError("test", "Ошибка валидации");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        ErrorResponse response = handler.handleValidationException(ex);
        assertEquals("Ошибка валидации", response.getError());
    }

    @Test
    void handleMissingHeaderException() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("X-Sharer-User-Id");

        ErrorResponse response = handler.handleMissingHeaderException(ex);
        assertTrue(response.getError().contains("X-Sharer-User-Id"));
    }
}