package cooperativa.vista;

import cooperativa.servicio.Banco;

import javax.swing.JFrame;

/** Ventana con una pestaña por panel. Solo habla con Banco. (M5 - Valeria) */
public class VentanaPrincipal extends JFrame {
    private final Banco banco;

    public VentanaPrincipal(Banco banco) {
        super("Cooperativa X - Gestión bancaria");
        this.banco = banco;
        // TODO M5: JTabbedPane con los paneles
    }
}
