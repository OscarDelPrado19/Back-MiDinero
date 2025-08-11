package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.MetaAhorroDTO;
import com.midinero.entity.MetaAhorro;
import com.midinero.entity.Usuario;
import com.midinero.repository.MetaAhorroRepository;
import com.midinero.repository.UsuarioRepository;
import com.midinero.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MetaAhorroService {

    @Autowired
    private MetaAhorroRepository metaAhorroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionService notificacionService;

    public ApiResponse<List<MetaAhorroDTO>> obtenerMetas() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            List<MetaAhorro> metas = metaAhorroRepository.findByUsuarioIdOrderByFechaInicioDesc(usuario.getId());
            
            List<MetaAhorroDTO> metasDTO = metas.stream()
                .map(this::convertirAMetaAhorroDTO)
                .collect(Collectors.toList());

            return ApiResponse.success("Metas obtenidas exitosamente", metasDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener las metas");
        }
    }

    public ApiResponse<MetaAhorroDTO> crearMeta(MetaAhorroDTO metaDTO) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            MetaAhorro meta = new MetaAhorro();
            meta.setUsuario(usuario);
            meta.setNombre(metaDTO.getNombre());
            meta.setMontoObjetivo(metaDTO.getMontoObjetivo());
            meta.setMontoActual(metaDTO.getMontoActual() != null ? metaDTO.getMontoActual() : 0.0);
            meta.setFechaInicio(metaDTO.getFechaInicio());
            meta.setFechaFin(metaDTO.getFechaFin());

            MetaAhorro metaGuardada = metaAhorroRepository.save(meta);
            MetaAhorroDTO responseDTO = convertirAMetaAhorroDTO(metaGuardada);

            return ApiResponse.success("Meta creada exitosamente", responseDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al crear la meta");
        }
    }

    public ApiResponse<MetaAhorroDTO> actualizarMeta(Long id, MetaAhorroDTO metaDTO) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            MetaAhorro meta = metaAhorroRepository.findById(id)
                .orElse(null);

            if (meta == null || !meta.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Meta no encontrada");
            }

            meta.setNombre(metaDTO.getNombre());
            meta.setMontoObjetivo(metaDTO.getMontoObjetivo());
            meta.setMontoActual(metaDTO.getMontoActual());
            meta.setFechaInicio(metaDTO.getFechaInicio());
            meta.setFechaFin(metaDTO.getFechaFin());

            // Verificar si se completó la meta
            if (meta.getMontoActual() >= meta.getMontoObjetivo() && !meta.getCompletada()) {
                meta.setCompletada(true);
                notificacionService.enviarNotificacionMetaCompletada(usuario.getId(), meta.getNombre());
            }

            MetaAhorro metaActualizada = metaAhorroRepository.save(meta);
            MetaAhorroDTO responseDTO = convertirAMetaAhorroDTO(metaActualizada);

            return ApiResponse.success("Meta actualizada exitosamente", responseDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al actualizar la meta");
        }
    }

    public ApiResponse<String> eliminarMeta(Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            MetaAhorro meta = metaAhorroRepository.findById(id)
                .orElse(null);

            if (meta == null || !meta.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Meta no encontrada");
            }

            metaAhorroRepository.delete(meta);
            return ApiResponse.success("Meta eliminada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al eliminar la meta");
        }
    }

    public void actualizarMetasConIngreso(Long usuarioId, Double montoIngreso) {
        try {
            List<MetaAhorro> metasActivas = metaAhorroRepository.findByUsuarioIdAndCompletada(usuarioId, false);
            
            for (MetaAhorro meta : metasActivas) {
                // Distribuir el ingreso proporcionalmente entre las metas activas
                Double porcentajeDistribucion = 0.1; // 10% del ingreso para cada meta
                Double montoParaMeta = montoIngreso * porcentajeDistribucion;
                
                meta.setMontoActual(meta.getMontoActual() + montoParaMeta);
                
                // Verificar si se completó la meta
                if (meta.getMontoActual() >= meta.getMontoObjetivo()) {
                    meta.setCompletada(true);
                    notificacionService.enviarNotificacionMetaCompletada(usuarioId, meta.getNombre());
                }
                
                metaAhorroRepository.save(meta);
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar metas con ingreso: " + e.getMessage());
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return usuarioRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private MetaAhorroDTO convertirAMetaAhorroDTO(MetaAhorro meta) {
        MetaAhorroDTO dto = new MetaAhorroDTO();
        dto.setId(meta.getId());
        dto.setNombre(meta.getNombre());
        dto.setMontoObjetivo(meta.getMontoObjetivo());
        dto.setMontoActual(meta.getMontoActual());
        dto.setFechaInicio(meta.getFechaInicio());
        dto.setFechaFin(meta.getFechaFin());
        dto.setCompletada(meta.getCompletada());
        
        // Calcular porcentaje completado
        if (meta.getMontoObjetivo() > 0) {
            dto.setPorcentajeCompletado((meta.getMontoActual() / meta.getMontoObjetivo()) * 100);
        } else {
            dto.setPorcentajeCompletado(0.0);
        }
        
        return dto;
    }
}
