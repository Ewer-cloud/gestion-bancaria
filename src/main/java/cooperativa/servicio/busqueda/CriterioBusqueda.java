package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

/** Estrategia de búsqueda: decide si una transacción cumple el criterio. (M3 - Ewer) */
public interface CriterioBusqueda {
    boolean cumple(Transaccion transaccion);
}
