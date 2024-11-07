package com.gracaconsultores.ReporteGeneralAfiliaciones.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {
    private int code;
    private String message;
    private Object data;
    private int status;
}
