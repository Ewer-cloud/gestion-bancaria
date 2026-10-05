package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.util.List;
import java.util.Optional;

/** Strategy: una regla que decide si una transacción es sospechosa. (M4 - Víctor) */
public interface ReglaFraude {
    /** Nombre corto para mostrarlo y para activarla o desactivarla. */
    String getNombre();

    /**
     * @param historial transacciones anteriores. Usar la fecha de la transacción, no LocalDateTime.now().
     */
    Optional<Alerta> evaluar(Transaccion transaccion, List<Transaccion> historial);
}
