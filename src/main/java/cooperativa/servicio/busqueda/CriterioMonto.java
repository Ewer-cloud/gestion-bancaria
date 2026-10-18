package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.math.BigDecimal;

/** Filtra por rango de monto. */
public class CriterioMonto implements CriterioBusqueda {
    private final BigDecimal minimo;
    private final BigDecimal maximo;

    public CriterioMonto(BigDecimal minimo, BigDecimal maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        BigDecimal monto = transaccion.getMonto();
        boolean sobreMinimo = minimo == null || monto.compareTo(minimo) >= 0;
        boolean bajoMaximo = maximo == null || monto.compareTo(maximo) <= 0;
        return sobreMinimo && bajoMaximo;
    }
}
