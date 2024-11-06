package com.example.transaction.controller;

import com.example.transaction.exception.TransactionException;
import com.example.transaction.model.ApiResponse;
import com.example.transaction.model.TransactionRequest;
import com.example.transaction.model.TransactionResponse;
import com.example.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/consulta")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> consultarTransacciones(
            @RequestBody @Valid TransactionRequest request) {
        try {
            List<TransactionResponse> data = transactionService.consultarTransacciones(request);
            return ResponseEntity.ok(new ApiResponse<>("1000", "Consulta ejecutada exitosamente", data));
        } catch (TransactionException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getCodRet(), e.getMessage(), null));
        }
    }
}