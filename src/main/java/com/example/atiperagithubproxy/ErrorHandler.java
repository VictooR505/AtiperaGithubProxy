package com.example.atiperagithubproxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
class ErrorHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    ResponseEntity<ErrorResponse> handleNotFound() {
        return ResponseEntity.status(404)
                .body(new ErrorResponse(404, "Github user not found"));
    }
}
