package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.CambiarPasswordDTO;
import com.midinero.dto.UsuarioResponseDTO;
import com.midinero.dto.UpdateUsuarioDTO;
import com.midinero.entity.Usuario;
import com.midinero.repository.UsuarioRepository;
import com.midinero.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse<UsuarioResponseDTO> obtenerPerfilUsuario() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            UsuarioResponseDTO responseDTO = convertirAUsuarioResponseDTO(usuario);
            return ApiResponse.success("Perfil obtenido exitosamente", responseDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener el perfil");
        }
    }

    public ApiResponse<String> cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) {
        // Validar que las contraseñas coincidan
        if (!cambiarPasswordDTO.getNuevaPassword().equals(cambiarPasswordDTO.getConfirmarPassword())) {
            return ApiResponse.error("Las contraseñas no coinciden");
        }

        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            // Verificar contraseña actual
            if (!passwordEncoder.matches(cambiarPasswordDTO.getPasswordActual(), usuario.getPassword())) {
                return ApiResponse.error("La contraseña actual es incorrecta");
            }

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(cambiarPasswordDTO.getNuevaPassword()));
            usuarioRepository.save(usuario);

            return ApiResponse.success("Contraseña actualizada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al cambiar la contraseña");
        }
    }

    public ApiResponse<UsuarioResponseDTO> actualizarPerfil(UpdateUsuarioDTO dto) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            // Email único (permitiendo que conserve su mismo email)
            if (usuarioRepository.existsByEmailAndIdNot(dto.getEmail(), usuario.getId())) {
                return ApiResponse.error("El email ya está registrado por otro usuario");
            }

            // Actualizar campos
            usuario.setNombreCompleto(dto.getNombreCompleto().trim());
            usuario.setEmail(dto.getEmail().trim());

            if (dto.getCarrera() != null) {
                String c = dto.getCarrera().trim();
                usuario.setCarrera(c.isEmpty() ? null : c);
            } else {
                usuario.setCarrera(null);
            }

            Usuario guardado = usuarioRepository.save(usuario);
            UsuarioResponseDTO responseDTO = convertirAUsuarioResponseDTO(guardado);
            return ApiResponse.success("Perfil actualizado exitosamente", responseDTO);

        } catch (Exception e) {
            return ApiResponse.error("Error al actualizar el perfil");
        }
    }

    public ApiResponse<String> eliminarCuenta() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            usuarioRepository.delete(usuario);
            return ApiResponse.success("Cuenta eliminada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al eliminar la cuenta");
        }
    }

    // ================== Helpers ==================

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return usuarioRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UsuarioResponseDTO convertirAUsuarioResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombreCompleto(usuario.getNombreCompleto());
        dto.setEmail(usuario.getEmail());
        dto.setCarrera(usuario.getCarrera());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setSaldo(usuario.getSaldo());
        return dto;
    }
}
