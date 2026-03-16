package com.example.demo.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Data
public class GoogleApiException extends RuntimeException {
    private final HttpStatus status;

    public GoogleApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }
}
