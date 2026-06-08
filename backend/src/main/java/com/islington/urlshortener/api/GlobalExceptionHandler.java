package com.islington.urlshortener.api;

import com.islington.urlshortener.exception.AliasAlreadyExistsException;
import com.islington.urlshortener.exception.ShortUrlNotFoundException;
import com.islington.urlshortener.exception.UnsafeUrlException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setDetail(ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Request body is invalid"));
        detail.setType(URI.create("https://errors.url-shortener.local/validation"));
        return detail;
    }

    @ExceptionHandler(UnsafeUrlException.class)
    ProblemDetail handleUnsafeUrl(UnsafeUrlException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Unsafe URL");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://errors.url-shortener.local/unsafe-url"));
        return detail;
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    ProblemDetail handleAliasConflict(AliasAlreadyExistsException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Alias already exists");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://errors.url-shortener.local/alias-conflict"));
        return detail;
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    ProblemDetail handleNotFound(ShortUrlNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Short URL not found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://errors.url-shortener.local/not-found"));
        return detail;
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        detail.setTitle("Service temporarily unavailable");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("https://errors.url-shortener.local/unavailable"));
        return detail;
    }
}
