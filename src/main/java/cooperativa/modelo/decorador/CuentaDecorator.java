package cooperativa.modelo.decorador;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;

import java.math.BigDecimal;

/** Base del Decorator: envuelve una cuenta y le agrega extras. (M1 extra - Daniel L.) */
public abstract class CuentaDecorator extends Cuenta {
    protected final Cuenta cuenta;

    protected CuentaDecorator(Cuenta cuenta) {
        super(cuenta.getNumero(), cuenta.getIdTitular(), cuenta.getSaldo(), cuenta.isBloqueada());
        this.cuenta = cuenta;
    }

    @Override
    public void retirar(BigDecimal monto) throws SaldoInsuficienteException, CuentaBloqueadaException {
        cuenta.retirar(monto);
    }

    @Override
    public String getTipo() {
        return cuenta.getTipo();
    }
}
