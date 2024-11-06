package com.example.transaction.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    @NotBlank(message = "tipoProducto no puede estar vacío")
    private String tipoProducto;
    
    @NotBlank(message = "fechaInicio no puede estar vacío")
    private String fechaInicio;
    
    @NotBlank(message = "fechaFin no puede estar vacío")
    private String fechaFin;
}