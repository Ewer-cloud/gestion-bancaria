package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Alerta si el monto supera un umbral. (M4 - Víctor) */
public class ReglaMontoAlto implements ReglaFraude {
    private final BigDecimal umbral;

    public ReglaMontoAlto(BigDecimal umbral) {
        this.umbral = umbral;
    }

    @Override
    public String getNombre() {
        return "Monto alto";
    }

    @Override
    public Optional<Alerta> evaluar(Transaccion transaccion, List<Transaccion> historial) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
