package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

/** Se cumple si se cumple al menos un criterio (meta). (M3 - Ewer) */
public class CriterioOr implements CriterioBusqueda {
    private final CriterioBusqueda[] criterios;

    public CriterioOr(CriterioBusqueda... criterios) {
        this.criterios = criterios;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
