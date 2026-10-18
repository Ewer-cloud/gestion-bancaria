package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CuentaCorrienteTest {
    private CuentaCorriente nuevaCuenta(String saldo) {
        return new CuentaCorriente("CO-001", "CLI-001", new BigDecimal(saldo), false);
    }

    @Test
    void retiroConSaldoSuficienteRestaDelSaldo() throws Exception {
        CuentaCorriente cuenta = nuevaCuenta("1000");
        cuenta.retirar(new BigDecimal("400"));
        assertEquals(0, new BigDecimal("600").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retiroPuedeSobregirarLaCuenta() throws Exception {
        CuentaCorriente cuenta = nuevaCuenta("1000");
        cuenta.retirar(new BigDecimal("1500"));
        assertEquals(0, new BigDecimal("-500").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retiroHastaElLimiteExactoEsPermitido() throws Exception {
        CuentaCorriente cuenta = nuevaCuenta("1000");
        cuenta.retirar(new BigDecimal("501000"));
        assertEquals(0, new BigDecimal("-500000").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retiroQueSuperaElLimiteLanzaExcepcion() {
        CuentaCorriente cuenta = nuevaCuenta("1000");
        assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.retirar(new BigDecimal("501001")));
        assertEquals(0, new BigDecimal("1000").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retirarDeCuentaBloqueadaLanzaExcepcion() {
        CuentaCorriente cuenta = nuevaCuenta("1000");
        cuenta.bloquear();
        assertThrows(CuentaBloqueadaException.class,
                () -> cuenta.retirar(new BigDecimal("100")));
    }

    @Test
    void tipoEsCorriente() {
        assertEquals("CORRIENTE", nuevaCuenta("0").getTipo());
    }
}

