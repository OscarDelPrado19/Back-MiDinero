package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.PresupuestoDTO;
import com.midinero.service.PresupuestoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    @Autowired
    private PresupuestoService presupuestoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PresupuestoDTO>>> obtenerPresupuestos() {
        ApiResponse<List<PresupuestoDTO>> response = presupuestoService.obtenerPresupuestos();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PresupuestoDTO>> crearPresupuesto(@Valid @RequestBody PresupuestoDTO presupuestoDTO) {
        ApiResponse<PresupuestoDTO> response = presupuestoService.crearPresupuesto(presupuestoDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PresupuestoDTO>> actualizarPresupuesto(
            @PathVariable Long id, 
            @Valid @RequestBody PresupuestoDTO presupuestoDTO) {
        ApiResponse<PresupuestoDTO> response = presupuestoService.actualizarPresupuesto(id, presupuestoDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarPresupuesto(@PathVariable Long id) {
        ApiResponse<String> response = presupuestoService.eliminarPresupuesto(id);
        return ResponseEntity.ok(response);
    }
}
