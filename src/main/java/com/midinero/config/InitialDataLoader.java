package com.midinero.config;

import com.midinero.entity.Glosario;
import com.midinero.entity.Tip;
import com.midinero.repository.GlosarioRepository;
import com.midinero.repository.TipRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final TipRepository tipRepo;
    private final GlosarioRepository glosarioRepo;

    public InitialDataLoader(TipRepository tipRepo, GlosarioRepository glosarioRepo) {
        this.tipRepo = tipRepo;
        this.glosarioRepo = glosarioRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedTips();
        seedGlosario();
    }

    private void seedTips() {
    List<Tip> defaults = List.of(
        new Tip("Registra tus movimientos", "Anota cada ingreso y gasto para mantener tu saldo actualizado."),
        new Tip("Filtra por fecha", "Usa el filtro de fechas en el historial para ver solo las transacciones de un día específico."),
        new Tip("Crea metas de ahorro", "Establece metas y abona regularmente para alcanzarlas más rápido."),
        new Tip("Consulta tu saldo", "Revisa tu saldo antes de realizar un gasto importante."),
        new Tip("Clasifica tus transacciones", "Usa categorías para identificar en qué gastas más y dónde puedes ahorrar."),
        new Tip("Corrige errores", "Si te equivocas, puedes anular o eliminar una transacción desde el historial."),
        new Tip("Actualiza tu perfil", "Mantén tus datos correctos y seguros para una mejor experiencia."),
        new Tip("Ajusta tus presupuestos", "Revisa y ajusta tus presupuestos mensualmente según tus necesidades."),
        new Tip("Abona solo a metas activas", "Solo puedes abonar a metas activas; las alcanzadas o canceladas no permiten abonos."),
        new Tip("Analiza tu historial", "Aprovecha el historial para analizar tus hábitos de gasto y mejorar tus finanzas."),
        new Tip("Evita sobregiros", "No gastes más de tu saldo disponible para mantener tus finanzas sanas."),
        new Tip("Usa el glosario", "Consulta el glosario si tienes dudas sobre algún término financiero de la app."),
        new Tip("Planifica tus gastos fijos", "Incluye tus gastos fijos en el presupuesto para evitar sorpresas."),
        new Tip("Revisa tus metas", "Cancela o ajusta metas de ahorro si cambian tus prioridades."),
        new Tip("Aprovecha los microahorros", "Redondea tus gastos y ahorra la diferencia para pequeñas metas.")
    );

        for (Tip t : defaults) {
            if (!tipRepo.existsByTituloIgnoreCase(t.getTitulo())) {
                tipRepo.save(t);
            }
        }
    }

    private void seedGlosario() {
    List<Glosario> defaults = List.of(
        new Glosario("Presupuesto", "Plan mensual donde defines cuánto gastarás en cada categoría."),
        new Glosario("Meta de ahorro", "Objetivo de dinero que quieres alcanzar guardando poco a poco."),
        new Glosario("Abono", "Dinero que agregas a una meta de ahorro activa."),
        new Glosario("Transacción", "Registro de un ingreso o gasto."),
        new Glosario("Saldo", "Dinero disponible después de sumar ingresos y restar gastos."),
        new Glosario("Categoría", "Tipo de gasto o ingreso (ejemplo: comida, transporte, educación)."),
        new Glosario("Ingreso", "Dinero que recibes y registras en la app."),
        new Glosario("Gasto", "Dinero que usas y registras en la app."),
        new Glosario("Anulada", "Transacción cancelada que ya no afecta tu saldo."),
        new Glosario("Soft-delete", "Eliminación lógica; la transacción no se muestra pero sigue en el historial."),
        new Glosario("Filtro", "Herramienta para ver solo ciertos movimientos (por tipo o fecha)."),
        new Glosario("Historial", "Lista de todas tus transacciones registradas en la app."),
        new Glosario("Meta activa", "Meta de ahorro que aún puedes abonar."),
        new Glosario("Meta alcanzada", "Meta de ahorro que ya cumpliste y no puedes modificar."),
        new Glosario("Meta cancelada", "Meta de ahorro que decidiste cancelar y ya no puedes abonar."),
        new Glosario("Gasto fijo", "Pago regular que debes hacer cada mes, como renta o internet."),
        new Glosario("Gasto variable", "Pago que cambia cada mes, como comida o transporte."),
        new Glosario("Microahorro", "Pequeñas cantidades que ahorras redondeando tus compras."),
        new Glosario("Perfil", "Tu información personal en la app, que puedes actualizar cuando quieras."),
        new Glosario("Abono mínimo", "Cantidad mínima que puedes agregar a una meta de ahorro."),
        new Glosario("Saldo insuficiente", "Cuando intentas gastar más de lo que tienes disponible."),
        new Glosario("Fecha de transacción", "Día en que registraste un ingreso o gasto."),
        new Glosario("Reporte", "Resumen visual de tus gastos, ingresos o presupuestos."),
        new Glosario("Cancelación de meta", "Acción de dejar sin efecto una meta de ahorro activa."),
        new Glosario("Edición de perfil", "Función para actualizar tu nombre, correo o carrera en la app.")
    );

        for (Glosario g : defaults) {
            if (!glosarioRepo.existsByTerminoIgnoreCase(g.getTermino())) {
                glosarioRepo.save(g);
            }
        }
    }
}
