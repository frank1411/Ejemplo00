package com.example.transaction.service;

import com.example.transaction.exception.TransactionException;
import com.example.transaction.model.TransactionDetailRequest;
import com.example.transaction.model.TransactionDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionDetailService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public TransactionDetailResponse consultarDetalle(TransactionDetailRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CONSULTA_DETALLE_TRANSACCION")
                .declareParameters(
                    new SqlParameter("p_id_orden", Types.NUMERIC),
                    new SqlParameter("p_tipo_producto", Types.VARCHAR),
                    new SqlOutParameter("p_out_data", Types.REF_CURSOR),
                    new SqlOutParameter("cod_ret", Types.VARCHAR),
                    new SqlOutParameter("de_ret", Types.VARCHAR)
                );

            MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_id_orden", request.getIdOrden())
                .addValue("p_tipo_producto", request.getTipoProducto());

            Map<String, Object> result = jdbcCall.execute(params);
            
            String codRet = (String) result.get("COD_RET");
            String deRet = (String) result.get("DE_RET");
            
            if (!"1000".equals(codRet)) {
                throw new TransactionException(deRet, codRet);
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("P_OUT_DATA");
            
            if (resultSet.isEmpty()) {
                throw new TransactionException("Transacción no encontrada", "1006");
            }
            
            return mapToDetailResponse(resultSet.get(0));
            
        } catch (TransactionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al consultar detalle de transacción", e);
            throw new TransactionException("Error no controlado: " + e.getMessage(), "1999");
        }
    }
    
    private TransactionDetailResponse mapToDetailResponse(Map<String, Object> row) {
        TransactionDetailResponse response = new TransactionDetailResponse();
        response.setIdOrden((Long) row.get("ID_ORDEN"));
        response.setRuf((String) row.get("RUF"));
        response.setTipoProducto((String) row.get("TIPO_PRODUCTO"));
        response.setFechaProceso((LocalDateTime) row.get("FECHA_PROCESO"));
        response.setMonto((BigDecimal) row.get("MONTO"));
        response.setEstado((String) row.get("ESTADO"));
        response.setCanal((String) row.get("CANAL"));
        response.setReferencia((String) row.get("REFERENCIA"));
        return response;
    }
}