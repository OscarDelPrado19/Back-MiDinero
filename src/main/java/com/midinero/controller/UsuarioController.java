package com.midinero.controller;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.CambiarPasswordDTO;
import com.midinero.dto.UpdateUsuarioDTO;
import com.midinero.dto.UsuarioResponseDTO;
import com.midinero.entity.Usuario;
import com.midinero.repository.UsuarioRepository;
import com.midinero.security.UserPrincipal;
import com.midinero.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ===== Perfil =====
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> obtenerPerfil() {
        ApiResponse<UsuarioResponseDTO> res = usuarioService.obtenerPerfilUsuario();
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> actualizarPerfil(
            @Valid @RequestBody UpdateUsuarioDTO dto) {
        ApiResponse<UsuarioResponseDTO> res = usuarioService.actualizarPerfil(dto);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    // ===== Password =====
    @PutMapping("/cambiar-password")
    public ResponseEntity<ApiResponse<String>> cambiarPassword(
            @Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) {
        ApiResponse<String> res = usuarioService.cambiarPassword(cambiarPasswordDTO);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    // ===== Eliminar cuenta =====
    @DeleteMapping("/eliminar-cuenta")
    public ResponseEntity<ApiResponse<String>> eliminarCuenta() {
        ApiResponse<String> res = usuarioService.eliminarCuenta();
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    // ===== Consultar saldo rápido para la app (usa Double) =====
    @GetMapping("/me/balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerSaldo() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

            Usuario u = usuarioRepository.findById(principal.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Double saldo = (u.getSaldo() == null) ? 0.0 : u.getSaldo();

            ApiResponse<Map<String, Object>> res =
                    ApiResponse.success("Saldo obtenido correctamente", Map.of("saldo", saldo));

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> res =
                    ApiResponse.error("Error al obtener el saldo");
            return ResponseEntity.badRequest().body(res);
        }
    }
}
