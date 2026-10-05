package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/** Alerta si la transacción ocurre fuera del horario normal (meta). (M4 - Víctor) */
public class ReglaHoraRara implements ReglaFraude {
    private final LocalTime inicioNormal;
    private final LocalTime finNormal;

    public ReglaHoraRara(LocalTime inicioNormal, LocalTime finNormal) {
        this.inicioNormal = inicioNormal;
        this.finNormal = finNormal;
    }

    @Override
    public String getNombre() {
        return "Hora rara";
    }

    @Override
    public Optional<Alerta> evaluar(Transaccion transaccion, List<Transaccion> historial) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
