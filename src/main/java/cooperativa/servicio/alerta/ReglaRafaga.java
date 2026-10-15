package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.Duration;
import java.time.LocalDateTime;
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
    public Optional<Alerta>evaluar(Transaccion transaccion, List<Transaccion> historial) {
        List<Transaccion> mismaCuenta = historial.stream()
                .filter(x -> x.getNumeroCuenta().equals(transaccion.getNumeroCuenta()))
                .toList();
        LocalDateTime inicio = transaccion.getFecha().minus(ventana);
        List<Transaccion> enVentana = mismaCuenta.stream()
                .filter(x-> !x.getFecha().isBefore(inicio))
                .toList();
        int total = enVentana.size() + 1;
        if (total >= cantidad){
            String mensaje = "Ráfaga en la cuenta " + transaccion.getNumeroCuenta()
                    +": " + total + " movimiento en " + ventana.toMinutes() + " minutos";
            return Optional.of(new Alerta(mensaje, NivelRiesgo.MEDIO, transaccion, transaccion.getFecha()));
        }
        return Optional.empty();
    }
}
