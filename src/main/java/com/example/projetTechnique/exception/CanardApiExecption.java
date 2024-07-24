package com.example.projetTechnique.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CanardApiExecption extends RuntimeException{

    @Getter
    private HttpStatus status;
    private String message;

    public CanardApiExecption(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public CanardApiExecption(String message, HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
