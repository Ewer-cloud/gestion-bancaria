package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.math.BigDecimal;

/** Filtra por rango de monto. (M3 - Ewer) */
public class CriterioMonto implements CriterioBusqueda {
    private final BigDecimal minimo;
    private final BigDecimal maximo;

    public CriterioMonto(BigDecimal minimo, BigDecimal maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
