package com.example.transaction.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionDetailRequest {
    @NotNull(message = "idOrden no puede ser nulo")
    private Long idOrden;
    
    @NotBlank(message = "tipoProducto no puede estar vacío")
    private String tipoProducto;
}