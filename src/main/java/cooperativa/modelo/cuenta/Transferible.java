package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/** Contrato de las cuentas que pueden participar en una transferencia. (M1 - Daniel L.) */
public interface Transferible {
    String getNumero();

    void depositar(BigDecimal monto) throws CuentaBloqueadaException;

    void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException;
}
