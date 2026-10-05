package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.AlertaListener;

import javax.swing.JPanel;

/** Lista las alertas con su nivel de riesgo. Es el Observer de la vista. (M5 - Valeria) */
public class PanelAlertas extends JPanel implements AlertaListener {
    private final Banco banco;

    public PanelAlertas(Banco banco) {
        this.banco = banco;
        // TODO M5: banco.getDetector().registrarListener(this)
    }

    @Override
    public void onAlerta(Alerta alerta) {
        // TODO M5: agregar la alerta a la tabla usando SwingUtilities.invokeLater
    }
}
