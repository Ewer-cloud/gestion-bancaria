package cooperativa.vista;

import cooperativa.servicio.Banco;
import cooperativa.vista.panel.PanelAlertas;
import cooperativa.vista.panel.PanelBusqueda;
import cooperativa.vista.panel.PanelClientes;
import cooperativa.vista.panel.PanelCuentas;
import cooperativa.vista.panel.PanelOperaciones;
import cooperativa.vista.panel.Refrescable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Ventana con menú lateral y una sección por panel. Solo habla con Banco. (M5 - Valeria) */
public class VentanaPrincipal extends JFrame {
    /** Una entrada del menú: su título, su descripción, el panel que muestra y su botón. */
    private record Seccion(String titulo, String descripcion, JComponent panel, BotonNavegacion boton) {
    }

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JPanel menu = new JPanel();
    private final JLabel titulo = Tema.titulo(" ");
    private final JLabel descripcion = Tema.textoSuave(" ");
    private Seccion actual;
    private Seccion seccionAlertas;
    private int alertasSinVer = 0;

    public VentanaPrincipal(Banco banco) {
        super("Cooperativa X - Gestión bancaria");

        setSize(1260, 760);
        setMinimumSize(new Dimension(1180, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Tema.FONDO);

        armarMenu();
        Seccion clientes = agregar("Clientes", "Registra y consulta a los clientes de la cooperativa", new PanelClientes(banco));
        agregar("Cuentas", "Abre cuentas y controla su estado", new PanelCuentas(banco));
        agregar("Operaciones", "Depósitos, retiros y transferencias", new PanelOperaciones(banco));
        agregar("Búsqueda", "Filtra el historial de transacciones", new PanelBusqueda(banco));
        seccionAlertas = agregar("Alertas", "Movimientos que el sistema considera sospechosos", new PanelAlertas(banco));

        // Otro Observer del mismo detector: marca el menú cuando llega una alerta y no se está viendo.
        banco.getDetector().registrarListener(alerta -> SwingUtilities.invokeLater(() -> {
            if (actual != seccionAlertas) {
                alertasSinVer++;
                seccionAlertas.boton().setInsignia(alertasSinVer);
            }
        }));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(26, 32, 6, 32));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        descripcion.setAlignmentX(LEFT_ALIGNMENT);
        encabezado.add(titulo);
        encabezado.add(Box.createVerticalStrut(4));
        encabezado.add(descripcion);

        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 32, 28, 32));

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(encabezado, BorderLayout.NORTH);
        derecha.add(contenido, BorderLayout.CENTER);

        add(menu, BorderLayout.WEST);
        add(derecha, BorderLayout.CENTER);

        mostrar(clientes);
    }

    private void armarMenu() {
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.LATERAL);
        menu.setPreferredSize(new Dimension(236, 0));
        menu.setBorder(BorderFactory.createEmptyBorder(28, 14, 20, 14));

        JLabel nombre = new JLabel("Cooperativa X");
        nombre.setFont(Tema.fuente(Font.BOLD, 17));
        nombre.setForeground(Color.WHITE);
        JLabel lema = new JLabel("Gestión bancaria");
        lema.setFont(Tema.fuente(Font.PLAIN, 12));
        lema.setForeground(new Color(0x8FB0B3));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(nombre);
        textos.add(lema);

        JPanel marca = new JPanel(new BorderLayout(12, 0));
        marca.setOpaque(false);
        marca.setAlignmentX(LEFT_ALIGNMENT);
        marca.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        marca.add(new Logo(), BorderLayout.WEST);
        marca.add(textos, BorderLayout.CENTER);
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        menu.add(marca);
        menu.add(Box.createVerticalStrut(32));
    }

    private Seccion agregar(String texto, String detalle, JComponent panel) {
        BotonNavegacion boton = new BotonNavegacion(texto);
        Seccion seccion = new Seccion(texto, detalle, panel, boton);
        boton.addActionListener(e -> mostrar(seccion));
        contenido.add(panel, texto);
        menu.add(boton);
        menu.add(Box.createVerticalStrut(4));
        return seccion;
    }

    /** Al entrar a una sección, el panel vuelve a leer sus datos (por ejemplo, clientes nuevos). */
    private void mostrar(Seccion seccion) {
        if (actual != null) {
            actual.boton().setSeleccionado(false);
        }
        actual = seccion;
        seccion.boton().setSeleccionado(true);
        tarjetas.show(contenido, seccion.titulo());
        titulo.setText(seccion.titulo());
        descripcion.setText(seccion.descripcion());
        if (seccion.panel() instanceof Refrescable refrescable) {
            refrescable.refrescar();
        }
        if (seccion == seccionAlertas) {
            alertasSinVer = 0;
            seccion.boton().setInsignia(0);
        }
    }

    /** Cuadrito redondeado con la inicial de la cooperativa. */
    private static class Logo extends JPanel {
        Logo() {
            setOpaque(false);
            setPreferredSize(new Dimension(40, 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(0x2DD4BF));
            g2.fillRoundRect(0, 0, 40, 40, 12, 12);
            g2.setColor(Tema.LATERAL);
            g2.setFont(Tema.fuente(Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("C", (40 - fm.stringWidth("C")) / 2, (40 - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
