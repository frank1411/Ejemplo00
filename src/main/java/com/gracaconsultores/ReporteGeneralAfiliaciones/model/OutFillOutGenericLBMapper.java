package com.gracaconsultores.ReporteGeneralAfiliaciones.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OutFillOutGenericLBMapper implements RowMapper<OutFillOutGenericLBModel> {

    @Override
    public OutFillOutGenericLBModel mapRow(ResultSet rs, int i) throws SQLException {

        OutFillOutGenericLBModel resp = new OutFillOutGenericLBModel();

        resp.setIdCode(rs.getString("ID_CODE"));
        resp.setDescripcion(rs.getString("DESCRIPCION"));
        return resp;
    }

}
