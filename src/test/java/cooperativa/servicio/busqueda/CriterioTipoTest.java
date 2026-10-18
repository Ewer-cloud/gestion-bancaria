package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriterioTipoTest {
    private Transaccion deTipo(TipoTransaccion tipo) {
        return new Transaccion("T-1", "AH-001", tipo, BigDecimal.TEN, LocalDateTime.of(2026, 10, 10, 10, 0));
    }

    @Test
    void cumpleSoloConElMismoTipo() {
        CriterioTipo criterio = new CriterioTipo(TipoTransaccion.RETIRO);
        assertTrue(criterio.cumple(deTipo(TipoTransaccion.RETIRO)));
        assertFalse(criterio.cumple(deTipo(TipoTransaccion.DEPOSITO)));
    }
}
