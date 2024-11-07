package com.gracaconsultores.ReporteGeneralAfiliaciones.service;

import com.gracaconsultores.ReporteGeneralAfiliaciones.model.InFillOutGenericLBModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.model.ResponseModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.repository.FillOutGenericLBRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FillOutGenericLBService {

    @Autowired
    FillOutGenericLBRepository fillOutGenericLBRepository;

    public ResponseModel getDatos(InFillOutGenericLBModel input){
        ResponseModel respuestaFinal = new ResponseModel();
        log.info("FillOutGenericLBService.input: {}", input);
        try {
                respuestaFinal = fillOutGenericLBRepository.getdatos(input);
                log.info("ConsultaTransaccionRepository.respuestaFinal: {}", respuestaFinal);
                return respuestaFinal;
        }catch (Exception e){
            log.error(e.getMessage());
            respuestaFinal.setCode(1010);
            respuestaFinal.setMessage("Error en consulta");
            respuestaFinal.setStatus(200);
            return respuestaFinal;
        }
    }
}
