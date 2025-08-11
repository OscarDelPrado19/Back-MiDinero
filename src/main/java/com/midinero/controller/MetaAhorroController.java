package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.MetaAhorroDTO;
import com.midinero.service.MetaAhorroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas-ahorro")
public class MetaAhorroController {

    @Autowired
    private MetaAhorroService metaAhorroService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MetaAhorroDTO>>> obtenerMetas() {
        ApiResponse<List<MetaAhorroDTO>> response = metaAhorroService.obtenerMetas();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MetaAhorroDTO>> crearMeta(@Valid @RequestBody MetaAhorroDTO metaDTO) {
        ApiResponse<MetaAhorroDTO> response = metaAhorroService.crearMeta(metaDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MetaAhorroDTO>> actualizarMeta(
            @PathVariable Long id, 
            @Valid @RequestBody MetaAhorroDTO metaDTO) {
        ApiResponse<MetaAhorroDTO> response = metaAhorroService.actualizarMeta(id, metaDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminarMeta(@PathVariable Long id) {
        ApiResponse<String> response = metaAhorroService.eliminarMeta(id);
        return ResponseEntity.ok(response);
    }
}
