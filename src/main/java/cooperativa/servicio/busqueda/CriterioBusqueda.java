package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

/** Estrategia de búsqueda: decide si una transacción cumple el criterio.*/
public interface CriterioBusqueda {
    boolean cumple(Transaccion transaccion);
}
