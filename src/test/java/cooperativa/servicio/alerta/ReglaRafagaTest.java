package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReglaRafagaTest {

    // Regla: 3 movimientos dentro de 5 minutos
    private final ReglaRafaga regla = new ReglaRafaga(3, Duration.ofMinutes(5));

    private final LocalDateTime base = LocalDateTime.of(2026, 10, 20, 10, 0);

    private Transaccion trx(String cuenta, LocalDateTime fecha) {
        return new Transaccion("TRX", cuenta, TipoTransaccion.DEPOSITO,
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
}