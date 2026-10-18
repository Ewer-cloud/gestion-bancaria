package cooperativa.servicio.alerta;

import cooperativa.modelo.transaccion.Transaccion;
import java.time.LocalDateTime;

/** Aviso de un posible fraude. (M4 - Víctor) */
public class Alerta {
    private final String mensaje;
    private final NivelRiesgo nivel;
    private final Transaccion transaccion;
    private final LocalDateTime fecha;

    public Alerta(String mensaje, NivelRiesgo nivel, Transaccion transaccion, LocalDateTime fecha) {
        this.mensaje = mensaje;
        this.nivel = nivel;
        this.transaccion = transaccion;
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public NivelRiesgo getNivel() {
        return nivel;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "[" + nivel + "]" + mensaje;
    }
}
