package com.gracaconsultores.ReporteGeneralAfiliaciones.controller;

import com.gracaconsultores.ReporteGeneralAfiliaciones.model.InFillOutGenericLBModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.model.ResponseModel;
import com.gracaconsultores.ReporteGeneralAfiliaciones.service.FillOutGenericLBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api")

public class FillOutGenericLBController {
    @Autowired
    FillOutGenericLBService fillOutGenericLBService;

    @PostMapping(value = "/fillOutGenericLB")
    public ResponseModel getDatos(@RequestBody InFillOutGenericLBModel input){
        log.info("FillOutGenericLBController.getDatos ({})",input.toString());
        return fillOutGenericLBService.getDatos(input);
    }

}
