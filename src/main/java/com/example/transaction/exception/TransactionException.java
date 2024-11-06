package com.example.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionException extends RuntimeException {
    private final String codRet;
    
    public TransactionException(String message, String codRet) {
        super(message);
        this.codRet = codRet;
    }
    
    public String getCodRet() {
        return codRet;
    }
}