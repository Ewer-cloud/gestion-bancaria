package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

/** Se cumple si se cumple todos los criterios (meta). (M3 - Ewer) */
public class CriterioAnd implements CriterioBusqueda {
    private final CriterioBusqueda[] criterios;

    public CriterioAnd(CriterioBusqueda... criterios) {
        this.criterios = criterios;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
