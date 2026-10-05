package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/** Alerta si hay N transacciones de la misma cuenta dentro de una ventana de tiempo. (M4 - Víctor) */
public class ReglaRafaga implements ReglaFraude {
    private final int cantidad;
    private final Duration ventana;

    public ReglaRafaga(int cantidad, Duration ventana) {
        this.cantidad = cantidad;
        this.ventana = ventana;
    }

    @Override
    public String getNombre() {
        return "Ráfaga";
    }

    @Override
    public Optional<Alerta> evaluar(Transaccion transaccion, List<Transaccion> historial) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
