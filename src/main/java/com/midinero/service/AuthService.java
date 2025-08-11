package com.midinero.service;

import com.midinero.dto.*;
import com.midinero.entity.TokenReset;
import com.midinero.entity.Usuario;
import com.midinero.repository.TokenResetRepository;
import com.midinero.repository.UsuarioRepository;
import com.midinero.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenResetRepository tokenResetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private EmailService emailService;

    public ApiResponse<UsuarioResponseDTO> registrarUsuario(UsuarioRegistroDTO registroDTO) {
        // Validar que las contraseñas coincidan
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            return ApiResponse.error("Las contraseñas no coinciden");
        }

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            return ApiResponse.error("El email ya está registrado");
        }

        // Crear nuevo usuario con saldo = 0.0 (Double)
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setCarrera(registroDTO.getCarrera());
        usuario.setFechaNacimiento(registroDTO.getFechaNacimiento());
        usuario.setSaldo(0.0); // <- clave

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        UsuarioResponseDTO responseDTO = convertirAUsuarioResponseDTO(usuarioGuardado);
        return ApiResponse.success("Usuario registrado exitosamente", responseDTO);
    }

    public ApiResponse<String> iniciarSesion(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            return ApiResponse.success("Inicio de sesión exitoso", jwt);
        } catch (Exception e) {
            return ApiResponse.error("Credenciales inválidas");
        }
    }

    public ApiResponse<String> cerrarSesion(String token) {
        try {
            tokenBlacklistService.blacklistToken(token);
            return ApiResponse.success("Sesión cerrada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al cerrar sesión");
        }
    }

    public ApiResponse<String> solicitarRecuperacionPassword(String email) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

            if (usuario == null) {
                // Por seguridad, no revelamos si el email existe o no
                return ApiResponse.success("Si el email existe, recibirás un enlace de recuperación");
            }

            // Generar token de recuperación
            String token = UUID.randomUUID().toString();
            TokenReset tokenReset = new TokenReset(usuario, token, LocalDateTime.now().plusHours(1));
            tokenResetRepository.save(tokenReset);

            // Enviar email
            emailService.enviarEmailRecuperacion(usuario.getEmail(), token);

            return ApiResponse.success("Si el email existe, recibirás un enlace de recuperación");
        } catch (Exception e) {
            return ApiResponse.error("Error al procesar la solicitud");
        }
    }

    public ApiResponse<String> resetearPassword(ResetPasswordDTO resetDTO) {
        // Validar que las contraseñas coincidan
        if (!resetDTO.getNuevaPassword().equals(resetDTO.getConfirmarPassword())) {
            return ApiResponse.error("Las contraseñas no coinciden");
        }

        try {
            TokenReset tokenReset = tokenResetRepository
                    .findByTokenAndUsadoFalseAndFechaExpiracionAfter(resetDTO.getToken(), LocalDateTime.now())
                    .orElse(null);

            if (tokenReset == null) {
                return ApiResponse.error("Token inválido o expirado");
            }

            // Actualizar contraseña
            Usuario usuario = tokenReset.getUsuario();
            usuario.setPassword(passwordEncoder.encode(resetDTO.getNuevaPassword()));
            usuarioRepository.save(usuario);

            // Marcar token como usado
            tokenReset.setUsado(true);
            tokenResetRepository.save(tokenReset);

            return ApiResponse.success("Contraseña actualizada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al resetear la contraseña");
        }
    }

    // === Mapper interno actualizado para incluir saldo (Double) ===
    private UsuarioResponseDTO convertirAUsuarioResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setCarrera(usuario.getCarrera());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setSaldo(usuario.getSaldo()); // Double
        return dto;
    }
}
