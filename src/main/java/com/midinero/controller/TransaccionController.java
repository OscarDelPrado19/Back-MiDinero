package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.TransaccionDTO;
import com.midinero.service.TransaccionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransaccionDTO>>> obtenerTransacciones() {
        ApiResponse<List<TransaccionDTO>> response = transaccionService.obtenerTransacciones();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransaccionDTO>> crearTransaccion(@Valid @RequestBody TransaccionDTO transaccionDTO) {
        ApiResponse<TransaccionDTO> response = transaccionService.crearTransaccion(transaccionDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransaccionDTO>> actualizarTransaccion(
            @PathVariable Long id, 
            @Valid @RequestBody TransaccionDTO transaccionDTO) {
        ApiResponse<TransaccionDTO> response = transaccionService.actualizarTransaccion(id, transaccionDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarTransaccion(@PathVariable Long id) {
        ApiResponse<String> response = transaccionService.eliminarTransaccion(id);
        return ResponseEntity.ok(response);
    }
}
