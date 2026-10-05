package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/** Cuenta corriente: permite sobregiro hasta un límite. (M1 - Daniel L.) */
public class CuentaCorriente extends Cuenta implements Transferible {
    public static final BigDecimal LIMITE_SOBREGIRO = new BigDecimal("500000");

    public CuentaCorriente(String numero, String idTitular, BigDecimal saldoInicial, boolean bloqueada) {
        super(numero, idTitular, saldoInicial, bloqueada);
    }

    @Override
    public String getTipo() {
        return "CORRIENTE";
    }

    @Override
    public void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException {
        // TODO M1: validar monto, bloqueo y que no se pase del límite de sobregiro
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
