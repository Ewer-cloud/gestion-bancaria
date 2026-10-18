package cooperativa.modelo.cuenta;

import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.excepcion.MontoInvalidoException;

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
        validarMonto(monto);
        if (bloqueada){
            throw new CuentaBloqueadaException("La cuenta " + numero + " está bloqueada");
        }
        saldo = saldo.add(monto);

    }

    protected void validarMonto(BigDecimal monto){
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor a cero");
        }
    }

    /** Cada tipo de cuenta valida el retiro a su manera (polimorfismo). */
    public abstract void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException;

    /** Devuelve "AHORRO" o "CORRIENTE" (es el valor de la columna tipo en la BD). */
    public abstract String getTipo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuenta)) return false;
        Cuenta otra = (Cuenta) o;
        return numero.equals(otra.numero);
    }

    @Override
    public int hashCode() {
        return numero.hashCode();
    }
    @Override
    public String toString() {
        return getTipo() + "[numero=" + numero
                + ", titular=" + idTitular + ", saldo=" + saldo
                + ", bloqueada=" + bloqueada + "]";
    }
}
