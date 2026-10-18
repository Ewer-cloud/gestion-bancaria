package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Alerta si hay N transacciones de la misma cuenta dentro de una ventana de tiempo.
 * La alerta salta al llegar al movimiento número N (contando el actual). (M4 - Víctor)
 */
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
        LocalDateTime inicio = transaccion.getFecha().minus(ventana);

        // Banco guarda la transacción antes de llamar al detector, así que el historial ya puede traer
        // la actual: se excluye para no contarla dos veces (se suma una sola vez más abajo).
        List<Transaccion> anterioresEnVentana = historial.stream()
                .filter(x -> x.getNumeroCuenta().equals(transaccion.getNumeroCuenta()))
                .filter(x -> !x.equals(transaccion))
                .filter(x -> !x.getFecha().isBefore(inicio))
                .toList();

        int total = anterioresEnVentana.size() + 1;
        if (total >= cantidad) {
            String mensaje = "Ráfaga en la cuenta " + transaccion.getNumeroCuenta()
                    + ": " + total + " movimientos en " + ventana.toMinutes() + " minutos";
            return Optional.of(new Alerta(mensaje, NivelRiesgo.MEDIO, transaccion, transaccion.getFecha()));
        }
        return Optional.empty();
    }
}
