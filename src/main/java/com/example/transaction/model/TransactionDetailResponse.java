package com.example.transaction.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDetailResponse {
    private Long idOrden;
    private String ruf;
    private String tipoProducto;
    private LocalDateTime fechaProceso;
    private BigDecimal monto;
    private String estado;
    private String canal;
    private String referencia;
}