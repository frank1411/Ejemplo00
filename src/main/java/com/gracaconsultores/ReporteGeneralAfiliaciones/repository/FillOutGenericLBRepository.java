package com.gracaconsultores.ReporteGeneralAfiliaciones.repository;

import com.gracaconsultores.ReporteGeneralAfiliaciones.model.InFillOutGenericLBModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.model.OutFillOutGenericLBModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.internal.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Map;

@Repository
@Slf4j
public class FillOutGenericLBRepository {

    @Autowired
    @Qualifier("jdbcTemplateBusiness")
    private JdbcTemplate jdbcTemplate;

        public ResponseModel getdatos(InFillOutGenericLBModel input) {
        ResponseModel respuesta = new ResponseModel();
        log.info("FillOutGenericLBRepository.getdatos ({})", input.toString());
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName("PCP")
                    .withCatalogName("PKG_WEB_LIST")
                    .withProcedureName("GENERIC")
                    .declareParameters(
                            new SqlParameter("P_PROCESS", OracleTypes.VARCHAR),
//                            new SqlParameter("P_TYPE", OracleTypes.VARCHAR),
                            new SqlOutParameter("P_OUT_CURSOR", OracleTypes.CURSOR),
                            new SqlOutParameter("P_OUT_STATUS", OracleTypes.VARCHAR)
                    );

            MapSqlParameterSource inputMap = new MapSqlParameterSource();

            inputMap.addValue("P_PROCESS", input.getProcess());
            Map<String, Object> resultMap = jdbcCall.execute(inputMap);
            ArrayList<OutFillOutGenericLBModel> r = (ArrayList<OutFillOutGenericLBModel>) resultMap.get("P_OUT_CURSOR");
            String outStatus = (String) resultMap.get("P_OUT_STATUS");
            log.info("outStatus: {}", outStatus);

            respuesta.setCode(1000);
            respuesta.setMessage(outStatus);
            respuesta.setStatus(200);
            respuesta.setData(r);
            return respuesta;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
