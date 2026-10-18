package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriterioMontoTest {

    private Transaccion conMonto(String monto) {
        return new Transaccion("T-1", "AH-001", TipoTransaccion.DEPOSITO,
                new BigDecimal(monto), LocalDateTime.of(2026, 10, 10, 10, 0));
    }

    @Test
    void montoDentroDelRangoCumple() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        assertTrue(criterio.cumple(conMonto("300")));
    }

    @Test
    void montoPorDebajoDelMinimoNoCumple() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        assertFalse(criterio.cumple(conMonto("99.99")));
    }

    @Test
    void montoPorEncimaDelMaximoNoCumple() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        assertFalse(criterio.cumple(conMonto("500.01")));
    }

    @Test
    void montoEnLosBordesCumple() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        assertTrue(criterio.cumple(conMonto("100")));
        assertTrue(criterio.cumple(conMonto("500")));
    }

    @Test
    void bordeConDistintaEscalaCumple() {
        // compareTo trata 100 y 100.00 como iguales (equals no lo haría)
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        assertTrue(criterio.cumple(conMonto("100.00")));
    }

    @Test
    void minimoNullSignificaSinLimiteInferior() {
        CriterioMonto criterio = new CriterioMonto(null, new BigDecimal("500"));
        assertTrue(criterio.cumple(conMonto("0.01")));
        assertFalse(criterio.cumple(conMonto("501")));
    }

    @Test
    void maximoNullSignificaSinLimiteSuperior() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), null);
        assertTrue(criterio.cumple(conMonto("1000000")));
        assertFalse(criterio.cumple(conMonto("99")));
    }

    @Test
    void ambosLimitesNullCumpleSiempre() {
        CriterioMonto criterio = new CriterioMonto(null, null);
        assertTrue(criterio.cumple(conMonto("1")));
    }
}