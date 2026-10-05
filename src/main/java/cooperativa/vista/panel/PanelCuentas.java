package cooperativa.vista.panel;

import cooperativa.servicio.Banco;

import javax.swing.JPanel;

/** Abrir cuentas, bloquear y desbloquear. (M5 - Valeria) */
public class PanelCuentas extends JPanel {
    private final Banco banco;

    public PanelCuentas(Banco banco) {
        this.banco = banco;
        // TODO M5
    }
}
