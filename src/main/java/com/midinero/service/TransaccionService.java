package com.midinero.service;

import com.midinero.dto.ApiResponse;
import com.midinero.dto.TransaccionDTO;
import com.midinero.entity.Transaccion;
import com.midinero.entity.Usuario;
import com.midinero.repository.TransaccionRepository;
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
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PresupuestoService presupuestoService;

    @Autowired
    private MetaAhorroService metaAhorroService;

    public ApiResponse<List<TransaccionDTO>> obtenerTransacciones() {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();
            List<Transaccion> transacciones =
                    transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId());

            List<TransaccionDTO> dtos = transacciones.stream()
                    .map(this::convertirATransaccionDTO)
                    .collect(Collectors.toList());

            return ApiResponse.success("Transacciones obtenidas exitosamente", dtos);
        } catch (Exception e) {
            return ApiResponse.error("Error al obtener las transacciones");
        }
    }

    public ApiResponse<TransaccionDTO> crearTransaccion(TransaccionDTO dto) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            // Validaciones básicas
            if (dto.getMonto() == null || dto.getMonto() <= 0) {
                return ApiResponse.error("El monto debe ser mayor a cero");
            }
            if (dto.getTipo() == null) {
                return ApiResponse.error("El tipo de transacción es obligatorio");
            }

            // Ajuste de saldo
            Double saldoActual = usuario.getSaldo() != null ? usuario.getSaldo() : 0.0;
            if (dto.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                if (saldoActual < dto.getMonto()) {
                    return ApiResponse.error("Saldo insuficiente");
                }
                usuario.setSaldo(saldoActual - dto.getMonto());
            } else { // INGRESO
                usuario.setSaldo(saldoActual + dto.getMonto());
            }
            usuarioRepository.save(usuario);

            // Crear transacción
            Transaccion t = new Transaccion();
            t.setUsuario(usuario);
            t.setTipo(dto.getTipo());
            t.setCategoria(dto.getCategoria());
            t.setMonto(dto.getMonto());       // entidad debe tener Double
            t.setDescripcion(dto.getDescripcion());
            Transaccion guardada = transaccionRepository.save(t);

            // Reglas adicionales
            if (t.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                presupuestoService.verificarPresupuesto(usuario.getId(), t.getCategoria(), t.getMonto());
            } else {
                metaAhorroService.actualizarMetasConIngreso(usuario.getId(), t.getMonto());
            }

            return ApiResponse.success("Transacción creada exitosamente", convertirATransaccionDTO(guardada));
        } catch (Exception e) {
            return ApiResponse.error("Error al crear la transacción");
        }
    }

    public ApiResponse<TransaccionDTO> actualizarTransaccion(Long id, TransaccionDTO dto) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            Transaccion t = transaccionRepository.findById(id).orElse(null);
            if (t == null || !t.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Transacción no encontrada");
            }

            if (dto.getMonto() == null || dto.getMonto() <= 0) {
                return ApiResponse.error("El monto debe ser mayor a cero");
            }
            if (dto.getTipo() == null) {
                return ApiResponse.error("El tipo de transacción es obligatorio");
            }

            // Revertir efecto anterior
            Double saldo = usuario.getSaldo() != null ? usuario.getSaldo() : 0.0;
            if (t.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                saldo += (t.getMonto() != null ? t.getMonto() : 0.0);
            } else {
                saldo -= (t.getMonto() != null ? t.getMonto() : 0.0);
            }

            // Aplicar nuevo efecto
            if (dto.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                if (saldo < dto.getMonto()) {
                    // restaurar efecto anterior para no dejar saldo inconsistente
                    if (t.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                        saldo -= (t.getMonto() != null ? t.getMonto() : 0.0);
                    } else {
                        saldo += (t.getMonto() != null ? t.getMonto() : 0.0);
                    }
                    usuario.setSaldo(saldo);
                    return ApiResponse.error("Saldo insuficiente");
                }
                saldo -= dto.getMonto();
            } else { // INGRESO
                saldo += dto.getMonto();
            }

            usuario.setSaldo(saldo);
            usuarioRepository.save(usuario);

            // Actualizar transacción
            t.setTipo(dto.getTipo());
            t.setCategoria(dto.getCategoria());
            t.setMonto(dto.getMonto());
            t.setDescripcion(dto.getDescripcion());
            Transaccion actualizada = transaccionRepository.save(t);

            // Reglas de negocio
            if (t.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                presupuestoService.verificarPresupuesto(usuario.getId(), t.getCategoria(), t.getMonto());
            } else {
                metaAhorroService.actualizarMetasConIngreso(usuario.getId(), t.getMonto());
            }

            return ApiResponse.success("Transacción actualizada exitosamente", convertirATransaccionDTO(actualizada));
        } catch (Exception e) {
            return ApiResponse.error("Error al actualizar la transacción");
        }
    }

    public ApiResponse<String> eliminarTransaccion(Long id) {
        try {
            Usuario usuario = obtenerUsuarioAutenticado();

            Transaccion t = transaccionRepository.findById(id).orElse(null);
            if (t == null || !t.getUsuario().getId().equals(usuario.getId())) {
                return ApiResponse.error("Transacción no encontrada");
            }

            // Revertir efecto en saldo
            Double saldo = usuario.getSaldo() != null ? usuario.getSaldo() : 0.0;
            if (t.getTipo() == Transaccion.TipoTransaccion.GASTO) {
                saldo += (t.getMonto() != null ? t.getMonto() : 0.0);
            } else {
                saldo -= (t.getMonto() != null ? t.getMonto() : 0.0);
            }
            usuario.setSaldo(saldo);
            usuarioRepository.save(usuario);

            t.setAnulada(true);
            transaccionRepository.save(t);
            return ApiResponse.success("Transacción anulada exitosamente");
        } catch (Exception e) {
            return ApiResponse.error("Error al eliminar la transacción");
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return usuarioRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private TransaccionDTO convertirATransaccionDTO(Transaccion t) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(t.getId());
        dto.setTipo(t.getTipo());
        dto.setCategoria(t.getCategoria());
        dto.setMonto(t.getMonto());     // Double
        dto.setDescripcion(t.getDescripcion());
        dto.setFecha(t.getFecha());
        dto.setAnulada(t.getAnulada());
        return dto;
    }
}
