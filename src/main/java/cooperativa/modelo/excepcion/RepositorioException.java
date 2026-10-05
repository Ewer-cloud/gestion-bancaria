package cooperativa.modelo.excepcion;

/** Envuelve los errores de acceso a datos (por ejemplo SQLException). Unchecked. (M2b) */
public class RepositorioException extends RuntimeException {
    public RepositorioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
