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

        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra la ventana

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", new JPanel());
        pestanas.addTab("Cuentas", new JPanel());
        pestanas.addTab("Operaciones", new JPanel());
        pestanas.addTab("Busquedas", new JPanel());
        pestanas.addTab("Alertas", new JPanel ());

        add(pestanas);

    }
}
