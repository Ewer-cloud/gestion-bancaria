package cooperativa.vista;

import cooperativa.servicio.Banco;
import cooperativa.vista.panel.PanelAlertas;
import cooperativa.vista.panel.PanelBusqueda;
import cooperativa.vista.panel.PanelClientes;
import cooperativa.vista.panel.PanelCuentas;
import cooperativa.vista.panel.PanelOperaciones;
import cooperativa.vista.panel.Refrescable;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Component;

/** Ventana con una pestaña por panel. Solo habla con Banco. (M5 - Valeria) */
public class VentanaPrincipal extends JFrame {
    private static final String TITULO_ALERTAS = "Alertas";

    private final Banco banco;
    private int alertasSinVer = 0;

    public VentanaPrincipal(Banco banco) {
        super("Cooperativa X - Gestión bancaria");
        this.banco = banco;

        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        PanelAlertas panelAlertas = new PanelAlertas(banco);
        pestanas.addTab("Clientes", new PanelClientes(banco));
        pestanas.addTab("Cuentas", new PanelCuentas(banco));
        pestanas.addTab("Operaciones", new PanelOperaciones(banco));
        pestanas.addTab("Búsqueda", new PanelBusqueda(banco));
        pestanas.addTab(TITULO_ALERTAS, panelAlertas);
        int posicionAlertas = pestanas.indexOfComponent(panelAlertas);

        // Al entrar a una pestaña, el panel vuelve a leer sus datos (por ejemplo, clientes nuevos).
        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual instanceof Refrescable refrescable) {
                refrescable.refrescar();
            }
            if (actual == panelAlertas) {
                alertasSinVer = 0;
                pestanas.setTitleAt(posicionAlertas, TITULO_ALERTAS);
            }
        });

        // Otro Observer del mismo detector: marca la pestaña cuando llega una alerta y no se está viendo.
        banco.getDetector().registrarListener(alerta -> SwingUtilities.invokeLater(() -> {
            if (pestanas.getSelectedComponent() != panelAlertas) {
                alertasSinVer++;
                pestanas.setTitleAt(posicionAlertas, TITULO_ALERTAS + " (" + alertasSinVer + " nueva(s))");
            }
        }));

        add(pestanas);
    }
}
