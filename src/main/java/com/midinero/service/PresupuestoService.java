package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.PresupuestoDTO;
import com.midinero.entity.Presupuesto;
import com.midinero.entity.Transaccion;
import com.midinero.entity.Usuario;
import com.midinero.repository.PresupuestoRepository;
import com.midinero.repository.TransaccionRepository;
import com.midinero.repository.UsuarioRepository;
import com.midinero.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PresupuestoService {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacionService notificacionService;

    public ApiResponse<List<PresupuestoDTO>> obtenerPresupuestos() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            List<Presupuesto> presupuestos = presupuestoRepository.findByUsuarioId(usuario.getId());
            
            List<PresupuestoDTO> presupuestosDTO = presupuestos.stream()
                .map(this::convertirAPresupuestoDTO)
                .collect(Collectors.toList());

            return ApiResponse.success("Presupuestos obtenidos exitosamente", presupuestosDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener los presupuestos");
        }
    }

    public ApiResponse<PresupuestoDTO> crearPresupuesto(PresupuestoDTO presupuestoDTO) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            // Verificar si ya existe un presupuesto para esa categoría y mes
            if (presupuestoRepository.findByUsuarioIdAndCategoriaAndMes(
                usuario.getId(), presupuestoDTO.getCategoria(), presupuestoDTO.getMes()).isPresent()) {
                return ApiResponse.error("Ya existe un presupuesto para esta categoría en este mes");
            }

            Presupuesto presupuesto = new Presupuesto();
            presupuesto.setUsuario(usuario);
            presupuesto.setCategoria(presupuestoDTO.getCategoria());
            presupuesto.setLimite(presupuestoDTO.getLimite());
            presupuesto.setMes(presupuestoDTO.getMes());

            Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);
            PresupuestoDTO responseDTO = convertirAPresupuestoDTO(presupuestoGuardado);

            return ApiResponse.success("Presupuesto creado exitosamente", responseDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al crear el presupuesto");
        }
    }

    public ApiResponse<PresupuestoDTO> actualizarPresupuesto(Long id, PresupuestoDTO presupuestoDTO) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElse(null);

            if (presupuesto == null || !presupuesto.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Presupuesto no encontrado");
            }

            presupuesto.setCategoria(presupuestoDTO.getCategoria());
            presupuesto.setLimite(presupuestoDTO.getLimite());
            presupuesto.setMes(presupuestoDTO.getMes());

            Presupuesto presupuestoActualizado = presupuestoRepository.save(presupuesto);
            PresupuestoDTO responseDTO = convertirAPresupuestoDTO(presupuestoActualizado);

            return ApiResponse.success("Presupuesto actualizado exitosamente", responseDTO);
        } catch (Exception e) {
            return ApiResponse.error("Error al actualizar el presupuesto");
        }
    }

    public ApiResponse<String> eliminarPresupuesto(Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            
            Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElse(null);

            if (presupuesto == null || !presupuesto.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Presupuesto no encontrado");
            }

            presupuestoRepository.delete(presupuesto);
            return ApiResponse.success("Presupuesto eliminado exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al eliminar el presupuesto");
        }
    }

    public void verificarPresupuesto(Long usuarioId, String categoria, Double montoGasto) {
        try {
            String mesActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            Presupuesto presupuesto = presupuestoRepository
                .findByUsuarioIdAndCategoriaAndMes(usuarioId, categoria, mesActual)
                .orElse(null);

            if (presupuesto != null) {
                Double gastoActual = calcularGastoActual(usuarioId, categoria, mesActual);
                
                if (gastoActual > presupuesto.getLimite()) {
                    notificacionService.enviarNotificacionPresupuestoExcedido(usuarioId, categoria, gastoActual, presupuesto.getLimite());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al verificar presupuesto: " + e.getMessage());
        }
    }

    private Double calcularGastoActual(Long usuarioId, String categoria, String mes) {
        LocalDateTime inicioMes = LocalDateTime.parse(mes + "-01T00:00:00");
        LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);

        Double gasto = transaccionRepository.sumByUsuarioIdAndTipoAndCategoriaAndFechaBetween(
            usuarioId, Transaccion.TipoTransaccion.GASTO, categoria, inicioMes, finMes);

        return gasto != null ? gasto : 0.0;
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return usuarioRepository.findById(userPrincipal.getId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private PresupuestoDTO convertirAPresupuestoDTO(Presupuesto presupuesto) {
        PresupuestoDTO dto = new PresupuestoDTO();
        dto.setId(presupuesto.getId());
        dto.setCategoria(presupuesto.getCategoria());
        dto.setLimite(presupuesto.getLimite());
        dto.setMes(presupuesto.getMes());
        dto.setGastoActual(calcularGastoActual(presupuesto.getUsuario().getId(), presupuesto.getCategoria(), presupuesto.getMes()));
        return dto;
    }
}
