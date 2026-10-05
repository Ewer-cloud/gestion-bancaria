package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.util.ArrayList;
import java.util.List;

/** Sujeto del Observer: aplica las reglas activas y avisa a los listeners. No conoce Swing. (M4 - Víctor) */
public class DetectorFraude {
    private final List<AlertaListener> listeners = new ArrayList<>();
    private final List<ReglaFraude> reglas = new ArrayList<>();

    public void registrarListener(AlertaListener listener) {
        // TODO M4
    }

    public void quitarListener(AlertaListener listener) {
        // TODO M4
    }

    public void agregarRegla(ReglaFraude regla) {
        // TODO M4
    }

    public void activarRegla(String nombre) {
        // TODO M4
    }

    public void desactivarRegla(String nombre) {
        // TODO M4
    }

    public List<ReglaFraude> getReglas() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    /** Evalúa la transacción con las reglas activas y notifica cada alerta generada. */
    public void analizar(Transaccion transaccion, List<Transaccion> historial) {
        // TODO M4
    }
}
