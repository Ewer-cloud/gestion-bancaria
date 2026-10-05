package cooperativa.modelo.excepcion;

/** Operación sobre una cuenta bloqueada. Checked. (M1 - Daniel L.) */
public class CuentaBloqueadaException extends Exception {
    public CuentaBloqueadaException(String mensaje) {
        super(mensaje);
    }
}
