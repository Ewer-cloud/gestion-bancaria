package cooperativa.modelo.excepcion;

/** Monto nulo, cero o negativo. Unchecked. (M1 - Daniel L.) */
public class MontoInvalidoException extends RuntimeException {
    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
