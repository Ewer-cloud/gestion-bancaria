package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CriterioCompuestoTest {
    private final Transaccion deposito1000 = new Transaccion(
            "T-1", "AH-001", TipoTransaccion.DEPOSITO,
            new BigDecimal("1000"), LocalDateTime.of(2026, 10, 20, 10, 0));
    private final CriterioBusqueda cumple = t -> true;
    private final CriterioBusqueda noCumple = t -> false;

    @Test
    void andSeCumpleSiTodosSeCumplen() {
        assertTrue(new CriterioAnd(cumple, cumple).cumple(deposito1000));
    }
    @Test
    void andFallaSiUnoNoSeCumple() {
        assertFalse(new CriterioAnd(cumple, noCumple).cumple(deposito1000));
    }
    @Test
    void andSinCriteriosDejaPasarTodo() {
        assertTrue(new CriterioAnd().cumple(deposito1000));
    }
    @Test
    void orSeCumpleSiAlMenosUnoSeCumple() {
        assertTrue(new CriterioOr(noCumple, cumple).cumple(deposito1000));
    }
    @Test
    void orFallaSiNingunoSeCumple() {
        assertFalse(new CriterioOr(noCumple, noCumple).cumple(deposito1000));
    }
    @Test
    void orSinCriteriosNoDejaPasarNada() {
        assertFalse(new CriterioOr().cumple(deposito1000));
    }
    @Test
    void andCombinaCriteriosReales() {
        CriterioBusqueda criterio = new CriterioAnd(
                new CriterioTipo(TipoTransaccion.DEPOSITO),
                new CriterioMonto(new BigDecimal("500"), new BigDecimal("2000")));
        assertTrue(criterio.cumple(deposito1000));
    }
    @Test
    void orCombinaCriteriosRealesYSePuedeAnidar() {
        CriterioBusqueda criterio = new CriterioOr(
                new CriterioTipo(TipoTransaccion.RETIRO),
                new CriterioAnd(
                        new CriterioTipo(TipoTransaccion.DEPOSITO),
                        new CriterioMonto(new BigDecimal("900"), null)));
        assertTrue(criterio.cumple(deposito1000));
    }
}
