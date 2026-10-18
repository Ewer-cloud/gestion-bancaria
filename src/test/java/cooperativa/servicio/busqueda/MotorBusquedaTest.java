package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotorBusquedaTest {

    private MotorBusqueda motor;
    private Transaccion t1; // 10 oct, 50
    private Transaccion t2; // 11 oct, 200
    private Transaccion t3; // 12 oct, 800
    private Transaccion t4; // 13 oct, 300

    @BeforeEach
    void preparar() {
        motor = new MotorBusqueda();
        t1 = nueva("T-1", "AH-001", "50", 10);
        t2 = nueva("T-2", "AH-001", "200", 11);
        t3 = nueva("T-3", "AH-002", "800", 12);
        t4 = nueva("T-4", "AH-002", "300", 13);
    }

    private Transaccion nueva(String id, String cuenta, String monto, int dia) {
        return new Transaccion(id, cuenta, TipoTransaccion.DEPOSITO,
                new BigDecimal(monto), LocalDateTime.of(2026, 10, dia, 9, 0));
    }

    @Test
    void sinCriteriosDevuelveTodoOrdenadoPorFecha() {
        // se entregan desordenadas a propósito
        List<Transaccion> resultado = motor.buscar(List.of(t3, t1, t4, t2));
        assertEquals(List.of(t1, t2, t3, t4), resultado);
    }

    @Test
    void conUnCriterioFiltraCorrectamente() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), new BigDecimal("500"));
        List<Transaccion> resultado = motor.buscar(List.of(t1, t2, t3, t4), criterio);
        assertEquals(List.of(t2, t4), resultado);
    }

    @Test
    void conDosCriteriosSoloPasanLasQueCumplenAmbos() {
        CriterioMonto monto = new CriterioMonto(new BigDecimal("100"), null);
        CriterioCliente cliente = new CriterioCliente(java.util.Set.of("AH-002"));
        List<Transaccion> resultado = motor.buscar(List.of(t1, t2, t3, t4), monto, cliente);
        assertEquals(List.of(t3, t4), resultado);
    }

    @Test
    void criterioQueNadaCumpleDevuelveListaVacia() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("10000"), null);
        assertTrue(motor.buscar(List.of(t1, t2, t3, t4), criterio).isEmpty());
    }

    @Test
    void listaVaciaDevuelveListaVacia() {
        assertTrue(motor.buscar(List.of(), new CriterioMonto(null, null)).isEmpty());
    }

    @Test
    void elResultadoSeOrdenaPorFechaAunConFiltro() {
        CriterioMonto criterio = new CriterioMonto(new BigDecimal("100"), null);
        List<Transaccion> resultado = motor.buscar(List.of(t4, t3, t2, t1), criterio);
        assertEquals(List.of(t2, t3, t4), resultado);
    }
}