package com.example.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String ruf;
    private String negociacion;
    private Integer volumenLotes;
    private Integer cli;
    private BigDecimal montoAbonado;
    private BigDecimal comisionCobrada;
    private Integer cargaBDVenLinea;
    private Integer cargaOtrosCanales;
    private Integer totalTransacciones;
    private BigDecimal montoAbonadoRev;
    private Integer cantidadReverso;
}