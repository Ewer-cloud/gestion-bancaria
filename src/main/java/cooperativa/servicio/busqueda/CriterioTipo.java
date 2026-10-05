package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;

/** Filtra por tipo de transacción. (M3 - Ewer) */
public class CriterioTipo implements CriterioBusqueda {
    private final TipoTransaccion tipo;

    public CriterioTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
