package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Alerta si el monto de una transaccion supera un umbral. (M4 - Victor) */
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
        if (transaccion.getMonto().compareTo(umbral) > 0) {
            String mensaje = "Monto alto en la cuenta " + transaccion.getNumeroCuenta()
                    + ": " + transaccion.getMonto() + " supera el umbral de " + umbral;
            return Optional.of(new Alerta(mensaje, NivelRiesgo.ALTO, transaccion, transaccion.getFecha()));
        }
        return Optional.empty();
    }
}
