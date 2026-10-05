package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.LocalDateTime;

/** Filtra por rango de fechas. (M3 - Ewer) */
public class CriterioFecha implements CriterioBusqueda {
    private final LocalDateTime desde;
    private final LocalDateTime hasta;

    public CriterioFecha(LocalDateTime desde, LocalDateTime hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
