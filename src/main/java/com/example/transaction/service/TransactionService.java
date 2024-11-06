package com.example.transaction.service;

import com.example.transaction.exception.TransactionException;
import com.example.transaction.model.TransactionRequest;
import com.example.transaction.model.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<TransactionResponse> consultarTransacciones(TransactionRequest request) {
        String sql = "BEGIN CONSULTA_DET_TRANS_NOM_PRO(?, ?, ?, ?, ?, ?); END;";
        
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CONSULTA_DET_TRANS_NOM_PRO")
                .declareParameters(
                    new SqlParameter("v_tipo_producto", Types.VARCHAR),
                    new SqlParameter("v_f_inicio", Types.VARCHAR),
                    new SqlParameter("v_f_fin", Types.VARCHAR),
                    new SqlOutParameter("P_OUT_DATA", Types.REF_CURSOR),
                    new SqlOutParameter("COD_RET", Types.VARCHAR),
                    new SqlOutParameter("DE_RET", Types.VARCHAR)
                );

            MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("v_tipo_producto", request.getTipoProducto())
                .addValue("v_f_inicio", request.getFechaInicio())
                .addValue("v_f_fin", request.getFechaFin());

            Map<String, Object> result = jdbcCall.execute(params);
            
            String codRet = (String) result.get("COD_RET");
            String deRet = (String) result.get("DE_RET");
            
            if (!"1000".equals(codRet)) {
                throw new TransactionException(deRet, codRet);
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("P_OUT_DATA");
            
            return resultSet.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
            
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al consultar transacciones", e);
            throw new TransactionException("Error no controlado: " + e.getMessage(), "1999");
        }
    }
    
    private TransactionResponse mapToTransactionResponse(Map<String, Object> row) {
        return new TransactionResponse(
            (String) row.get("RUF"),
            (String) row.get("NEGOCIACION"),
            (Integer) row.get("VOLUMENLOTES"),
            (Integer) row.get("CLI"),
            (BigDecimal) row.get("MONTO_ABONADO"),
            (BigDecimal) row.get("COMISION_COBRADA"),
            (Integer) row.get("CARGA_BDVENLINEA"),
            (Integer) row.get("CARGA_OTROS_CANALES"),
            (Integer) row.get("TOTAL_TRANSACCIONES"),
            (BigDecimal) row.get("MONTOABONADOREV"),
            (Integer) row.get("CANTIDADREVERSO")
        );
    }
}