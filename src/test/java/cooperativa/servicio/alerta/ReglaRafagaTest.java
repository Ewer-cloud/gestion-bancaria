package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReglaRafagaTest {
    // Regla: 3 movimientos dentro de 5 minutos
    private final ReglaRafaga regla = new ReglaRafaga(3, Duration.ofMinutes(5));
    private final LocalDateTime base = LocalDateTime.of(2026, 10, 20, 10, 0);
    private int contador = 0;

    // Cada transacción lleva un id distinto, porque Transaccion.equals compara por id.
    private Transaccion trx(String cuenta, LocalDateTime fecha) {
        return new Transaccion("TRX-" + (++contador), cuenta, TipoTransaccion.DEPOSITO,
                new BigDecimal("1000"), fecha);
    }

    @Test
    void menosMovimientosQueElMaximoNoGeneraAlerta() {
        Transaccion actual = trx("AH-001", base.plusMinutes(2));
        List<Transaccion> historial = List.of(trx("AH-001", base));
        assertTrue(regla.evaluar(actual, historial).isEmpty());
    }

    @Test
    void exactamenteElMaximoDentroDeLaVentanaGeneraAlerta() {
        Transaccion actual = trx("AH-001", base.plusMinutes(3));
        List<Transaccion> historial = List.of(
                trx("AH-001", base),
                trx("AH-001", base.plusMinutes(1)));
        Optional<Alerta> alerta = regla.evaluar(actual, historial);
        assertTrue(alerta.isPresent());
        assertEquals(NivelRiesgo.MEDIO, alerta.get().getNivel());
    }

    @Test
    void movimientosFueraDeLaVentanaNoGeneranAlerta() {
        Transaccion actual = trx("AH-001", base.plusHours(3));
        List<Transaccion> historial = List.of(
                trx("AH-001", base),
                trx("AH-001", base.plusMinutes(1)));
        assertTrue(regla.evaluar(actual, historial).isEmpty());
    }

    @Test
    void movimientosDeOtraCuentaNoGeneranAlerta() {
        Transaccion actual = trx("AH-001", base.plusMinutes(3));
        List<Transaccion> historial = List.of(
                trx("AH-999", base),
                trx("AH-999", base.plusMinutes(1)));
        assertTrue(regla.evaluar(actual, historial).isEmpty());
    }

    @Test
    void movimientoJustoEnElBordeDeLaVentanaSiCuenta() {
        Transaccion actual = trx("AH-001", base.plusMinutes(5));
        List<Transaccion> historial = List.of(
                trx("AH-001", base),                  // exactamente 5 minutos antes
                trx("AH-001", base.plusMinutes(2)));
        assertTrue(regla.evaluar(actual, historial).isPresent());
    }

    // Así lo llama Banco: la transacción actual ya está guardada y viene dentro del historial.
    @Test
    void siElHistorialYaIncluyeLaActualNoSeCuentaDosVeces() {
        List<Transaccion> historial = new ArrayList<>();
        Transaccion primera = trx("AH-001", base);
        historial.add(primera);
        assertTrue(regla.evaluar(primera, historial).isEmpty());

        Transaccion segunda = trx("AH-001", base.plusMinutes(1));
        historial.add(segunda);
        assertTrue(regla.evaluar(segunda, historial).isEmpty());   // 2 movimientos: todavía no es ráfaga

        Transaccion tercera = trx("AH-001", base.plusMinutes(2));
        historial.add(tercera);
        assertTrue(regla.evaluar(tercera, historial).isPresent()); // el tercero sí
    }
}
