package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Sujeto del Observer: aplica las reglas activas y avisa a los listeners. No conoce Swing. (M4 - Víctor) */
public class DetectorFraude {
    private final List<AlertaListener> listeners = new ArrayList<>();
    private final List<ReglaFraude> reglas = new ArrayList<>();
    private final Set<String> reglasDesactivadas = new HashSet<>();

    public void registrarListener(AlertaListener listener) {
        listeners.add(listener);
    }

    public void quitarListener(AlertaListener listener) {
        listeners.remove(listener);
    }

    public void agregarRegla(ReglaFraude regla) {
        reglas.add(regla);
    }

    public void activarRegla(String nombre) {
        reglasDesactivadas.remove(nombre);
    }

    public void desactivarRegla(String nombre) {
        reglasDesactivadas.add(nombre);
    }

    public List<ReglaFraude> getReglas() {
        return Collections.unmodifiableList(reglas);
    }

    /** Evalúa la transacción con las reglas activas y notifica cada alerta generada. */
    public void analizar(Transaccion transaccion, List<Transaccion> historial) {
        for (ReglaFraude regla : reglas) {
            if (reglasDesactivadas.contains(regla.getNombre())) {
                continue;
            }
            Optional<Alerta> alerta = regla.evaluar(transaccion, historial);
            alerta.ifPresent(this::avisar);
        }
    }

    private void avisar(Alerta alerta) {
        for (AlertaListener listener : listeners) {
            listener.onAlerta(alerta);
        }
    }
}