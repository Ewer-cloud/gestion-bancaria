package cooperativa.vista.panel;

import cooperativa.servicio.Banco;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelOperaciones extends JPanel {
    private final Banco banco;

    public PanelOperaciones(Banco banco) {
        this.banco = banco;
        add(new JLabel("Operaciones(en construcción)"));

    }
}
