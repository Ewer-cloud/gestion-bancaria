package cooperativa.vista.panel;

import cooperativa.servicio.Banco;

import javax.swing.*;

public class PanelCuentas extends JPanel {
    private final Banco banco;

    public PanelCuentas(Banco banco) {
        this.banco = banco;
        add(new JLabel("Cuentas (en construcción)"));
    }
}
