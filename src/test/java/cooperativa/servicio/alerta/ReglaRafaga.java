package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class ReglaRafagaTest {

    private final ReglaRafaga regla = new ReglaRafaga(3, Duration.ofMinutes(5));

    // Fabrica una transaccion inventada. Ajusta el constructor al real.
    private Transaccion trx(String cuenta, LocalDateTime fecha) {
        return new Transaccion(______);
    }

    @Test
    void menosMovimientosQueElMaximoNoGeneraAlerta() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 10, 0);
        Transaccion actual = trx("AH-001", base.plusMinutes(2));
        List<Transaccion> historial = List.of(trx("AH-001", base));

        assertTrue(regla.evaluar(actual, historial).______());
    }
}