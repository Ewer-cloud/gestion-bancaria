package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Filtra una lista de transacciones con uno o varios criterios (Streams). No accede al repositorio. (M3 - Ewer) */
public class MotorBusqueda {
    public List<Transaccion> buscar(List<Transaccion> transacciones, CriterioBusqueda... criterios) {
        return transacciones.stream()
                .filter(t -> Arrays.stream(criterios).allMatch(criterioBusqueda -> criterioBusqueda.cumple(t)))
                .sorted(Comparator.comparing(Transaccion::getFecha))
                .toList();
    }
}
