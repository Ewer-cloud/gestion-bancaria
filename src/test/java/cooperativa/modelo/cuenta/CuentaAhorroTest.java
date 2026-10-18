package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.MontoInvalidoException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class CuentaAhorroTest {
    private CuentaAhorro nuevaCuenta(String saldo) {
        return new CuentaAhorro("AH-001", "CLI-001", new BigDecimal(saldo), false);
    }
    @Test
    void depositarSumaAlSaldo() throws Exception {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        cuenta.depositar(new BigDecimal("500"));
        assertEquals(0, new BigDecimal("1500").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retiroCorrectoRestaDelSaldo() throws Exception {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        cuenta.retirar(new BigDecimal("400"));
        assertEquals(0, new BigDecimal("600").compareTo(cuenta.getSaldo()));
    }

    @Test
    void retiroConSaldoInsuficienteLanzaExcepcion() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        assertThrows(SaldoInsuficienteException.class,
                () -> cuenta.retirar(new BigDecimal("1001")));
        assertEquals(0, new BigDecimal("1000").compareTo(cuenta.getSaldo()));
    }

    @Test
    void depositarEnCuentaBloqueadaLanzaExcepcion() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        cuenta.bloquear();
        assertThrows(CuentaBloqueadaException.class,
                () -> cuenta.depositar(new BigDecimal("100")));
    }

    @Test
    void retirarDeCuentaBloqueadaLanzaExcepcion() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        cuenta.bloquear();
        assertThrows(CuentaBloqueadaException.class,
                () -> cuenta.retirar(new BigDecimal("100")));
    }

    @Test
    void montoCeroLanzaMontoInvalido() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        assertThrows(MontoInvalidoException.class,
                () -> cuenta.depositar(BigDecimal.ZERO));
    }

    @Test
    void montoNegativoLanzaMontoInvalido() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        assertThrows(MontoInvalidoException.class,
                () -> cuenta.retirar(new BigDecimal("-5")));
    }

    @Test
    void calcularInteresNoModificaElSaldo() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        BigDecimal interes = cuenta.calcularInteres();
        assertEquals(0, new BigDecimal("10").compareTo(interes));
        assertEquals(0, new BigDecimal("1000").compareTo(cuenta.getSaldo()));
    }

    @Test
    void aplicarInteresSumaElInteresAlSaldo() {
        CuentaAhorro cuenta = nuevaCuenta("1000");
        cuenta.aplicarInteres();
        assertEquals(0, new BigDecimal("1010").compareTo(cuenta.getSaldo()));
    }
}

