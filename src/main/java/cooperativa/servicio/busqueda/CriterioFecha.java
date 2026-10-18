package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.LocalDateTime;

/** Filtra por rango de fechas. Un límite null significa "sin límite".  - Ewer) */
public class CriterioFecha implements CriterioBusqueda {
    private final LocalDateTime desde;
    private final LocalDateTime hasta;

    public CriterioFecha(LocalDateTime desde, LocalDateTime hasta) {
        this.desde = desde;
        this.hasta = hasta;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        LocalDateTime fecha = transaccion.getFecha();
        boolean despuesDeDesde = desde == null || !fecha.isBefore(desde);
        boolean antesDeHasta = hasta == null || !fecha.isAfter(hasta);
        return despuesDeDesde && antesDeHasta;
    }
}
