package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/** Cuenta de ahorro: no admite saldo negativo y genera intereses. (M1 - Daniel L.) */
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
        // TODO M1: validar monto, bloqueo y saldo suficiente
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public BigDecimal calcularInteres() {
        // TODO M1: saldo * TASA_MENSUAL
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void aplicarInteres() {
        // TODO M1: sumar el interés al saldo
    }
}
