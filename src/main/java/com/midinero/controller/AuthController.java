package com.midinero.controller;

import com.midinero.dto.*;
import com.midinero.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UsuarioResponseDTO>> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO registroDTO) {
        ApiResponse<UsuarioResponseDTO> response = authService.registrarUsuario(registroDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) {
        ApiResponse<String> response = authService.iniciarSesion(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> cerrarSesion(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        if (StringUtils.hasText(jwt)) {
            ApiResponse<String> response = authService.cerrarSesion(jwt);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(ApiResponse.error("Token no encontrado"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> solicitarRecuperacionPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        ApiResponse<String> response = authService.solicitarRecuperacionPassword(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetearPassword(@Valid @RequestBody ResetPasswordDTO resetDTO) {
        ApiResponse<String> response = authService.resetearPassword(resetDTO);
        return ResponseEntity.ok(response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
