package cooperativa.vista.panel;

import cooperativa.servicio.Banco;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelBusqueda extends JPanel {
    private final Banco banco;

    public PanelBusqueda(Banco banco) {
        this.banco = banco;
        add(new JLabel("Búsqueda (en construcción)"));

    }
}
