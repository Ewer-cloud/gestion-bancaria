package cooperativa.vista;

import cooperativa.servicio.Banco;
import cooperativa.vista.panel.PanelAlertas;
import cooperativa.vista.panel.PanelBusqueda;
import cooperativa.vista.panel.PanelClientes;
import cooperativa.vista.panel.PanelCuentas;
import cooperativa.vista.panel.PanelOperaciones;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/** Ventana con una pestaña por panel. Solo habla con Banco. (M5 - Valeria) */
public class VentanaPrincipal extends JFrame {
    private final Banco banco;

    public VentanaPrincipal(Banco banco) {
        super("Cooperativa X - Gestión bancaria");
        this.banco = banco;

        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Clientes", new PanelClientes(banco));
        pestanas.addTab("Cuentas", new PanelCuentas(banco));
        pestanas.addTab("Operaciones", new PanelOperaciones(banco));
        pestanas.addTab("Búsqueda", new PanelBusqueda(banco));
        pestanas.addTab("Alertas", new PanelAlertas(banco));

        add(pestanas);
    }
}