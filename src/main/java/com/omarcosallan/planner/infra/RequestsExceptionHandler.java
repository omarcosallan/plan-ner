package com.omarcosallan.planner.infra;

import com.omarcosallan.planner.exceptions.ErrorMessage;
import com.omarcosallan.planner.exceptions.InvalidDateException;
import com.omarcosallan.planner.exceptions.ResourceNotFoundException;
import com.omarcosallan.planner.exceptions.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<StandardError> invalidDateException(InvalidDateException e, HttpServletRequest request) {
        String error = "Date invalid.";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Campo(s) inválido(s).";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ErrorMessage> errorsMessages = e.getBindingResult().getFieldErrors().stream().map(err ->
                new ErrorMessage(err.getDefaultMessage(), err.getField())).toList();

        StandardError err = new StandardError(Instant.now(), status.value(), error, errorsMessages, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
