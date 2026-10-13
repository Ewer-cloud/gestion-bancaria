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
        validarMonto(monto);
        if (bloqueada) {
            throw new CuentaBloqueadaException("La cuenta " + numero + " está bloqueada");
        }
        BigDecimal disponible = saldo.add(LIMITE_SOBREGIRO);
        if (monto.compareTo(disponible) > 0) {
            throw new SaldoInsuficienteException("Supera el límite de sobregiro en la cuenta " + numero);
        }
        saldo = saldo.subtract(monto);
    }
}
