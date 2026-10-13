package cooperativa.vista;

import cooperativa.servicio.Banco;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Ventana con una pestaña por panel. Solo habla con Banco. (M5 - Valeria) */
public class VentanaPrincipal extends JFrame {
    private final Banco banco;

    public VentanaPrincipal(Banco banco) {
        super("Cooperativa X - Gestión bancaria");
        this.banco = banco;
        // TODO M5: JTabbedPane con los paneles

        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra la ventana

        JTabbedPane Pestañas = new JTabbedPane();
        Pestañas.addTab("Clientes", new JPanel());
        Pestañas.addTab("Cuentas", new JPanel());
        Pestañas.addTab("Operaciones", new JPanel());
        Pestañas.addTab("Búsquedas", new JPanel());
        Pestañas.addTab("Alertas", new JPanel ());

        add(Pestañas);

    }
}
