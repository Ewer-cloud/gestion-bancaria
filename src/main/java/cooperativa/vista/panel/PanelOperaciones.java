package cooperativa.vista.panel;

import cooperativa.servicio.Banco;

import javax.swing.JPanel;

/** Depositar, retirar y transferir. (M5 - Valeria) */
public class PanelOperaciones extends JPanel {
    private final Banco banco;

    public PanelOperaciones(Banco banco) {
        this.banco = banco;
        // TODO M5
    }
}
