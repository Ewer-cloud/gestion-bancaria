package cooperativa.modelo.decorador;

import cooperativa.modelo.cuenta.Cuenta;

/** Cuenta con seguro (extra). (M1 extra - Daniel L.) */
public class CuentaConSeguro extends CuentaDecorator {
    public CuentaConSeguro(Cuenta cuenta) {
        super(cuenta);
    }

    // TODO M1 extra: costo o beneficio del seguro
}
