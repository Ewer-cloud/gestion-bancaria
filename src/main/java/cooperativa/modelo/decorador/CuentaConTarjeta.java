package cooperativa.modelo.decorador;

import cooperativa.modelo.cuenta.Cuenta;

/** Cuenta con tarjeta (extra). (M1 extra - Daniel L.) */
public class CuentaConTarjeta extends CuentaDecorator {
    public CuentaConTarjeta(Cuenta cuenta) {
        super(cuenta);
    }

    // TODO M1 extra: costo o beneficio de la tarjeta
}
