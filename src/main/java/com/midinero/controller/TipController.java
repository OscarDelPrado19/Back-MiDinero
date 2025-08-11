package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.entity.Tip;
import com.midinero.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tips")
public class TipController {

    @Autowired
    private TipService tipService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Tip>>> obtenerTips() {
        ApiResponse<List<Tip>> response = tipService.obtenerTips();
        return ResponseEntity.ok(response);
    }
}
