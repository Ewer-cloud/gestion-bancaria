package cooperativa.modelo.excepcion;

/** Retiro o transferencia por encima de lo disponible. Checked. (M1 - Daniel L.) */
public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
