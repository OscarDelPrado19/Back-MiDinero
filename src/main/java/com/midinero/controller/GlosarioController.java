package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.entity.Glosario;
import com.midinero.service.GlosarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/glosario")
public class GlosarioController {

    @Autowired
    private GlosarioService glosarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Glosario>>> obtenerGlosario() {
        ApiResponse<List<Glosario>> response = glosarioService.obtenerGlosario();
        return ResponseEntity.ok(response);
    }
}
