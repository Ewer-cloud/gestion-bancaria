package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReglaMontoAltoTest {
    private final ReglaMontoAlto regla = new ReglaMontoAlto(new BigDecimal("1000"));

    private Transaccion trx(String monto) {
        return new Transaccion("TRX-1", "AH-001", TipoTransaccion.RETIRO,
                new BigDecimal(monto), LocalDateTime.of(2026, 10, 20, 10, 0));
    }

    @Test
    void montoPorEncimaDelUmbralGeneraAlertaAlta() {
        Optional<Alerta> alerta = regla.evaluar(trx("1000.01"), List.of());
        assertTrue(alerta.isPresent());
        assertEquals(NivelRiesgo.ALTO, alerta.get().getNivel());
    }

    @Test
    void montoIgualAlUmbralNoGeneraAlerta() {
        assertTrue(regla.evaluar(trx("1000"), List.of()).isEmpty());
        assertTrue(regla.evaluar(trx("1000.00"), List.of()).isEmpty()); // otra escala, mismo valor
    }

    @Test
    void montoPorDebajoDelUmbralNoGeneraAlerta() {
        assertTrue(regla.evaluar(trx("10"), List.of()).isEmpty());
    }

    @Test
    void laAlertaUsaLaFechaDeLaTransaccion() {
        Transaccion t = trx("5000");
        assertEquals(t.getFecha(), regla.evaluar(t, List.of()).orElseThrow().getFecha());
    }
}
