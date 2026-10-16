package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.AlertaListener;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class PanelAlertas extends JPanel implements AlertaListener {
    private final Banco banco;

    public PanelAlertas(Banco banco) {
        this.banco = banco;
        add(new JLabel("Alertas (en construcción)"));
        // TODO M5: banco.getDetector().registrarListener(this)
    }

    @Override
    public void onAlerta(Alerta alerta) {
        // TODO M5: agregar la alerta a la tabla usando SwingUtilities.invokeLater
    }
}
