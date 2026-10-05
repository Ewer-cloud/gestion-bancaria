package cooperativa.servicio.alerta;

/** Observer: quien quiera recibir alertas implementa esta interfaz (la vista, por ejemplo). (M4 - Víctor) */
public interface AlertaListener {
    void onAlerta(Alerta alerta);
}
