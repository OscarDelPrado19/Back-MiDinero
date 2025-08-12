
package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.MetaAhorroDTO;
import com.midinero.service.MetaAhorroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

     @PostMapping("/{id}/abonar")
    public ResponseEntity<ApiResponse<MetaAhorroDTO>> abonarMeta(
            @PathVariable Long id,
            @RequestParam("monto") Double monto) {
        ApiResponse<MetaAhorroDTO> response = metaAhorroService.abonarMeta(id, monto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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
