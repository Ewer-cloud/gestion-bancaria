package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/**
 * Cuenta de ahorro: no admite saldo negativo y genera intereses. (M1 - Daniel L.)
 */
public class CuentaAhorro extends Cuenta implements Transferible, GeneradoraDeIntereses {
    public static final BigDecimal TASA_MENSUAL = new BigDecimal("0.01");

    public CuentaAhorro(String numero, String idTitular, BigDecimal saldoInicial, boolean bloqueada) {
        super(numero, idTitular, saldoInicial, bloqueada);
    }

    @Override
    public String getTipo() {
        return "AHORRO";
    }

    @Override
    public void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException {
        validarMonto(monto);
        if (bloqueada) {
            throw new CuentaBloqueadaException("La cuenta " + numero + " está bloqueada");
        }
        if (monto.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la cuenta " + numero);
        }
        saldo = saldo.subtract(monto);
    }

    @Override
    public BigDecimal calcularInteres() {
        return saldo.multiply(TASA_MENSUAL);
    }

    @Override
    public void aplicarInteres() {
        saldo = saldo.add(calcularInteres());
    }
}
