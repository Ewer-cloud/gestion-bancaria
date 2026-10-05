package cooperativa.servicio.busqueda;

import cooperativa.modelo.transaccion.Transaccion;

import java.util.Set;

/** Filtra las transacciones de las cuentas de un cliente (Banco entrega los números de cuenta). (M3 - Ewer) */
public class CriterioCliente implements CriterioBusqueda {
    private final Set<String> numerosDeCuenta;

    public CriterioCliente(Set<String> numerosDeCuenta) {
        this.numerosDeCuenta = numerosDeCuenta;
    }

    @Override
    public boolean cumple(Transaccion transaccion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
