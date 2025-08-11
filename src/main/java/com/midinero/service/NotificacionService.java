package com.midinero.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificacionService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacionPresupuestoExcedido(Long usuarioId, String categoria, Double gastoActual, Double limite) {
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("tipo", "PRESUPUESTO_EXCEDIDO");
        notificacion.put("titulo", "Presupuesto Excedido");
        notificacion.put("mensaje", String.format("Has excedido el presupuesto de %s. Gasto actual: $%.2f, Límite: $%.2f", 
            categoria, gastoActual, limite));
        notificacion.put("categoria", categoria);
        notificacion.put("gastoActual", gastoActual);
        notificacion.put("limite", limite);
        notificacion.put("fecha", LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
            usuarioId.toString(), 
            "/notifications", 
            notificacion
        );
    }

    public void enviarNotificacionMetaCompletada(Long usuarioId, String nombreMeta) {
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("tipo", "META_COMPLETADA");
        notificacion.put("titulo", "¡Meta Alcanzada!");
        notificacion.put("mensaje", String.format("¡Felicidades! Has completado tu meta de ahorro: %s", nombreMeta));
        notificacion.put("nombreMeta", nombreMeta);
        notificacion.put("fecha", LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
            usuarioId.toString(), 
            "/notifications", 
            notificacion
        );
    }
}
