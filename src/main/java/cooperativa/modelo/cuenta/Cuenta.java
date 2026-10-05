package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/**
 * Cuenta bancaria base. Guarda solo el id del titular (String), no un objeto Cliente.
 * (M1 - Daniel L.)
 */
public abstract class Cuenta {
    protected final String numero;
    protected final String idTitular;
    protected BigDecimal saldo;
    protected boolean bloqueada;

    protected Cuenta(String numero, String idTitular, BigDecimal saldoInicial, boolean bloqueada) {
        this.numero = numero;
        this.idTitular = idTitular;
        this.saldo = saldoInicial;
        this.bloqueada = bloqueada;
    }

    public String getNumero() {
        return numero;
    }

    public String getIdTitular() {
        return idTitular;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void bloquear() {
        this.bloqueada = true;
    }

    public void desbloquear() {
        this.bloqueada = false;
    }

    /** Valida (monto > 0, cuenta no bloqueada) y suma al saldo. */
    public void depositar(BigDecimal monto) throws CuentaBloqueadaException {
        // TODO M1: validar y actualizar el saldo
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    /** Cada tipo de cuenta valida el retiro a su manera (polimorfismo). */
    public abstract void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException;

    /** Devuelve "AHORRO" o "CORRIENTE" (es el valor de la columna tipo en la BD). */
    public abstract String getTipo();

    // TODO M1: equals y hashCode por numero, y toString legible
}
