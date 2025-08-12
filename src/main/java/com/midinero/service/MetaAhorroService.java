
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

        public ApiResponse<MetaAhorroDTO> abonarMeta(Long metaId, Double monto) {
        try {
            if (monto == null || monto <= 0) {
                return ApiResponse.error("El monto debe ser mayor a cero");
            }
            Usuario usuario = obtenerUsuarioAutenticado();
            MetaAhorro meta = metaAhorroRepository.findById(metaId)
                    .orElseThrow(() -> new RuntimeException("Meta no encontrada"));
            if (!meta.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("No tienes permiso para modificar esta meta");
            }
            if (meta.getEstado() != MetaAhorro.EstadoMeta.ACTIVA) {
                return ApiResponse.error("Solo puedes abonar a metas activas");
            }
            if (usuario.getSaldo() < monto) {
                return ApiResponse.error("Saldo insuficiente");
            }
            double nuevoMonto = meta.getMontoActual() + monto;
            if (nuevoMonto > meta.getMontoObjetivo()) {
                monto = meta.getMontoObjetivo() - meta.getMontoActual(); // Solo abona lo necesario para completar
                nuevoMonto = meta.getMontoObjetivo();
            }
            // Validación extra: nunca permitir saldo negativo
            if (usuario.getSaldo() - monto < 0) {
                return ApiResponse.error("Saldo insuficiente. No se permite saldo negativo.");
            }
            usuario.setSaldo(usuario.getSaldo() - monto);
            meta.setMontoActual(nuevoMonto);
            if (nuevoMonto >= meta.getMontoObjetivo()) {
                meta.setEstado(MetaAhorro.EstadoMeta.COMPLETADA);
            }
            usuarioRepository.save(usuario);
            metaAhorroRepository.save(meta);
            MetaAhorroDTO dto = convertirAMetaAhorroDTO(meta);
            return ApiResponse.success("Abono realizado exitosamente", dto);
        } catch (Exception e) {
            return ApiResponse.error("Error al abonar a la meta: " + e.getMessage());
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
            if (meta.getMontoActual() >= meta.getMontoObjetivo() && meta.getEstado() != MetaAhorro.EstadoMeta.COMPLETADA) {
                meta.setEstado(MetaAhorro.EstadoMeta.COMPLETADA);
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
            if (meta.getEstado() == MetaAhorro.EstadoMeta.CANCELADA) {
                return ApiResponse.error("La meta ya está cancelada");
            }
            if (meta.getEstado() == MetaAhorro.EstadoMeta.COMPLETADA) {
                metaAhorroRepository.delete(meta);
                return ApiResponse.success("Meta completada eliminada exitosamente");
            }
            // Si la meta está activa, cancelar y devolver dinero
            double montoADevolver = meta.getMontoActual() != null ? meta.getMontoActual() : 0.0;
            if (montoADevolver > 0) {
                usuario.setSaldo(usuario.getSaldo() + montoADevolver);
            }
            meta.setEstado(MetaAhorro.EstadoMeta.CANCELADA);
            meta.setMontoActual(0.0);
            usuarioRepository.save(usuario);
            metaAhorroRepository.save(meta);
            return ApiResponse.success("Meta cancelada y dinero devuelto al saldo");
        } catch (Exception e) {
            return ApiResponse.error("Error al eliminar/cancelar la meta: " + e.getMessage());
        }
    }

    public void actualizarMetasConIngreso(Long usuarioId, Double montoIngreso) {
        try {
            // Cambiar a buscar por estado ACTIVA
            List<MetaAhorro> metasActivas = metaAhorroRepository.findByUsuarioIdAndEstado(usuarioId, MetaAhorro.EstadoMeta.ACTIVA);
            
            for (MetaAhorro meta : metasActivas) {
                // Distribuir el ingreso proporcionalmente entre las metas activas
                Double porcentajeDistribucion = 0.1; // 10% del ingreso para cada meta
                Double montoParaMeta = montoIngreso * porcentajeDistribucion;
                
                meta.setMontoActual(meta.getMontoActual() + montoParaMeta);
                
                // Verificar si se completó la meta
                if (meta.getMontoActual() >= meta.getMontoObjetivo() && meta.getEstado() != MetaAhorro.EstadoMeta.COMPLETADA) {
                    meta.setEstado(MetaAhorro.EstadoMeta.COMPLETADA);
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
            dto.setEstado(meta.getEstado() != null ? meta.getEstado().name() : "ACTIVA");
        
        // Calcular porcentaje completado
        if (meta.getMontoObjetivo() > 0) {
            dto.setPorcentajeCompletado(Math.min(100.0, (meta.getMontoActual() / meta.getMontoObjetivo()) * 100));
        } else {
            dto.setPorcentajeCompletado(0.0);
        }
        return dto;
    }
}
