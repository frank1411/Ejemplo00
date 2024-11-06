package com.example.transaction.controller;

import com.example.transaction.exception.TransactionException;
import com.example.transaction.model.ApiResponse;
import com.example.transaction.model.TransactionDetailRequest;
import com.example.transaction.model.TransactionDetailResponse;
import com.example.transaction.service.TransactionDetailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@Slf4j
public class TransactionDetailController {

    @Autowired
    private TransactionDetailService transactionDetailService;
    
    @PostMapping("/detalle")
    public ResponseEntity<ApiResponse<TransactionDetailResponse>> consultarDetalle(
            @RequestBody @Valid TransactionDetailRequest request) {
        try {
            TransactionDetailResponse data = transactionDetailService.consultarDetalle(request);
            return ResponseEntity.ok(new ApiResponse<>("1000", "Consulta ejecutada exitosamente", data));
        } catch (TransactionException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(e.getCodRet(), e.getMessage(), null));
        }
    }
}