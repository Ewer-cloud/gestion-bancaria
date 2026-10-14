package cooperativa.modelo.cuenta;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaFactoryTest {

    @Test
    void creaCuentaAhorroCuandoElTipoEsAhorro() {
        Cuenta cuenta = CuentaFactory.crear("AHORRO", "AH-001", "CLI-001", new BigDecimal("1000"), false);
        assertInstanceOf(CuentaAhorro.class, cuenta);
        assertEquals("AHORRO", cuenta.getTipo());
    }

    @Test
    void creaCuentaCorrienteCuandoElTipoEsCorriente() {
        Cuenta cuenta = CuentaFactory.crear("CORRIENTE", "CO-001", "CLI-001", new BigDecimal("1000"), false);
        assertInstanceOf(CuentaCorriente.class, cuenta);
        assertEquals("CORRIENTE", cuenta.getTipo());
    }

    @Test
    void conservaLosDatosAlReconstruirDesdeLaBD() {
        Cuenta cuenta = CuentaFactory.crear("AHORRO", "AH-002", "CLI-007", new BigDecimal("250"), true);
        assertEquals("AH-002", cuenta.getNumero());
        assertEquals("CLI-007", cuenta.getIdTitular());
        assertEquals(0, new BigDecimal("250").compareTo(cuenta.getSaldo()));
        assertTrue(cuenta.isBloqueada());
    }

    @Test
    void tipoDesconocidoLanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CuentaFactory.crear("EMPRESARIAL", "X-001", "CLI-001", BigDecimal.ZERO, false));
    }

    @Test
    void tipoNuloLanzaIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> CuentaFactory.crear(null, "X-001", "CLI-001", BigDecimal.ZERO, false));
    }

    @Test
    void dosCuentasConElMismoNumeroSonIguales() {
        Cuenta a = CuentaFactory.crear("AHORRO", "AH-001", "CLI-001", new BigDecimal("100"), false);
        Cuenta b = CuentaFactory.crear("AHORRO", "AH-001", "CLI-002", new BigDecimal("999"), true);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void dosCuentasConDistintoNumeroNoSonIguales() {
        Cuenta a = CuentaFactory.crear("AHORRO", "AH-001", "CLI-001", BigDecimal.TEN, false);
        Cuenta b = CuentaFactory.crear("AHORRO", "AH-002", "CLI-001", BigDecimal.TEN, false);
        assertNotEquals(a, b);
    }
}
