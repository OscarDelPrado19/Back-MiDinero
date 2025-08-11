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
                new Tip("Regla 50/30/20", "50% necesidades, 30% gustos, 20% ahorro/deuda."),
                new Tip("Fondo de emergencia", "Ahorra 3–6 meses de gastos básicos."),
                new Tip("Gastos hormiga", "Detecta compras pequeñas repetidas y ponles tope."),
                new Tip("Paga primero tu ahorro", "Automatiza el ahorro al inicio del mes."),
                new Tip("Presupuesto realista", "Usa promedios de 3 meses para categorías."),
                new Tip("Metas SMART", "Específicas, medibles, alcanzables, relevantes y con fecha."),
                new Tip("Evita deuda cara", "Liquida primero las tarjetas con mayor tasa."),
                new Tip("Suscripciones", "Cancela servicios que no uses o estén duplicadas."),
                new Tip("Compra informada", "Compara precios y aplica la regla de 24 horas."),
                new Tip("Microahorros", "Redondea compras y ahorra la diferencia.")
        );

        for (Tip t : defaults) {
            if (!tipRepo.existsByTituloIgnoreCase(t.getTitulo())) {
                tipRepo.save(t);
            }
        }
    }

    private void seedGlosario() {
        List<Glosario> defaults = List.of(
                new Glosario("Ahorro", "Ingreso guardado para metas o emergencias."),
                new Glosario("Presupuesto", "Plan mensual de gasto por categorías."),
                new Glosario("Gasto fijo", "Pago regular (renta, internet)."),
                new Glosario("Gasto variable", "Pago que cambia cada mes (comida, transporte)."),
                new Glosario("Gastos hormiga", "Pequeños gastos frecuentes que suman mucho."),
                new Glosario("Fondo de emergencia", "Ahorro para imprevistos sin endeudarte."),
                new Glosario("Interés", "Costo del dinero prestado o ganancia por ahorrar."),
                new Glosario("Tasa de interés", "Porcentaje de costo o rendimiento anual."),
                new Glosario("Deuda", "Obligación de pago (créditos, tarjetas)."),
                new Glosario("Crédito", "Dinero prestado a devolver con intereses."),
                new Glosario("Ingreso", "Dinero que recibes (salario, beca, ventas)."),
                new Glosario("Meta financiera", "Objetivo de dinero con plazo (viaje, equipo)."),
                new Glosario("Saldo", "Ingresos menos gastos (positivo/negativo)."),
                new Glosario("Liquidez", "Qué tan fácil puedes usar tu dinero ya."),
                new Glosario("Inflación", "Aumento general de precios."),
                new Glosario("Amortización", "Pago gradual de una deuda en el tiempo."),
                new Glosario("Morosidad", "Retraso en pagos."),
                new Glosario("Score crediticio", "Puntaje de comportamiento de pagos.")
        );

        for (Glosario g : defaults) {
            if (!glosarioRepo.existsByTerminoIgnoreCase(g.getTermino())) {
                glosarioRepo.save(g);
            }
        }
    }
}
