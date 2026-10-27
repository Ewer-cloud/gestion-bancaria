package cooperativa.vista;

import cooperativa.modelo.transaccion.TipoTransaccion;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Colores, fuentes y fábricas de componentes con el estilo de la aplicación. Solo presentación. */
public final class Tema {
    public static final Color FONDO = new Color(0xF3F5F9);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color BORDE = new Color(0xDDE3EC);
    public static final Color TEXTO = new Color(0x1B2430);
    public static final Color TEXTO_SUAVE = new Color(0x6B7686);
    public static final Color PRIMARIO = new Color(0x0F766E);
    public static final Color PRIMARIO_HOVER = new Color(0x0B5F59);
    public static final Color LATERAL = new Color(0x0E2A2D);
    public static final Color LATERAL_HOVER = new Color(0x17393D);
    public static final Color PELIGRO = new Color(0xC62828);
    public static final Color EXITO = new Color(0x1B7F3B);
    public static final Color AVISO = new Color(0xC26A00);
    private static final Color FILA_ALTERNA = new Color(0xF8FAFC);
    private static final Color SELECCION = new Color(0xD7EFEB);
    private static final Color ENCABEZADO = new Color(0xF1F5F9);

    private static final Font BASE = elegirFuente();

    private Tema() {
    }

    /** Usa la primera fuente moderna que exista en el equipo; si no hay ninguna, la sans-serif de Java. */
    private static Font elegirFuente() {
        Set<String> disponibles = Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .collect(Collectors.toSet());
        for (String nombre : new String[]{"Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", "Noto Sans", "DejaVu Sans"}) {
            if (disponibles.contains(nombre)) {
                return new Font(nombre, Font.PLAIN, 13);
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    }

    public static Font fuente(int estilo, float tamano) {
        return BASE.deriveFont(estilo, tamano);
    }

    /** Se llama una sola vez, antes de crear la ventana. */
    public static void instalar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está, se queda con el aspecto por defecto: la app funciona igual.
        }
        UIManager.put("control", FONDO);
        UIManager.put("nimbusBase", PRIMARIO);
        UIManager.put("nimbusFocus", PRIMARIO);
        UIManager.put("nimbusSelectionBackground", PRIMARIO);
        UIManager.put("nimbusLightBackground", SUPERFICIE);
        UIManager.put("text", TEXTO);
        UIManager.put("defaultFont", fuente(Font.PLAIN, 13));
    }

    // ---------- Textos ----------

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 24));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 15));
        l.setForeground(TEXTO);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static JLabel textoSuave(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 13));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    // ---------- Formularios ----------

    /** Etiqueta pequeña arriba y el componente abajo. Con etiqueta null deja el hueco para alinear botones con los campos. */
    public static JPanel campo(String etiqueta, JComponent componente) {
        if (componente instanceof JTextField texto) {
            estilizarCampo(texto);
        } else if (componente instanceof JComboBox<?> combo) {
            estilizarCombo(combo);
        }
        JPanel panel = new JPanel(new BorderLayout(0, 6)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        panel.setOpaque(false);
        JLabel l = new JLabel(etiqueta == null ? " " : etiqueta);
        l.setFont(fuente(Font.BOLD, 12));
        l.setForeground(TEXTO_SUAVE);
        panel.add(l, BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /** Fila horizontal de campos, pegada a la izquierda. */
    public static JPanel fila(Component... componentes) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i = 0; i < componentes.length; i++) {
            if (i > 0) {
                fila.add(javax.swing.Box.createHorizontalStrut(14));
            }
            fila.add(componentes[i]);
        }
        return fila;
    }

    private static void estilizarCampo(JTextField campo) {
        campo.setFont(fuente(Font.PLAIN, 13));
        campo.setForeground(TEXTO);
        campo.setBackground(SUPERFICIE);
        campo.setBorder(bordeCampo(BORDE));
        campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 38));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                campo.setBorder(bordeCampo(PRIMARIO));
            }

            @Override
            public void focusLost(FocusEvent e) {
                campo.setBorder(bordeCampo(BORDE));
            }
        });
    }

    private static javax.swing.border.Border bordeCampo(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    /** Combo plano con borde como el de los campos y una flechita fina. Conserva el renderizador que ya tuviera. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void estilizarCombo(JComboBox combo) {
        ListCellRenderer anterior = combo.getRenderer();
        combo.setUI(new ComboTema());
        combo.setRenderer((ListCellRenderer) (lista, valor, indice, seleccionado, foco) -> {
            Component c = anterior.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
            if (c instanceof JComponent j) {
                j.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            }
            return c;
        });
        combo.setFont(fuente(Font.PLAIN, 13));
        combo.setBackground(SUPERFICIE);
        combo.setForeground(TEXTO);
        combo.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        combo.setPreferredSize(new Dimension(Math.max(combo.getPreferredSize().width, 220), 38));
    }

    private static class ComboTema extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton boton = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXTO_SUAVE);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g2.drawPolyline(new int[]{cx - 4, cx, cx + 4}, new int[]{cy - 2, cy + 2, cy - 2}, 3);
                    g2.dispose();
                }
            };
            boton.setBorder(BorderFactory.createEmptyBorder());
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            boton.setOpaque(false);
            boton.setPreferredSize(new Dimension(34, 34));
            return boton;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle limites, boolean foco) {
            g.setColor(SUPERFICIE);
            g.fillRect(limites.x, limites.y, limites.width, limites.height);
        }
    }

    /** Barra de scroll delgada, sin flechas, con la manija redondeada y gris. */
    private static class ScrollTema extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(0xC3CBD6);
            trackColor = SUPERFICIE;
        }

        @Override
        protected JButton createDecreaseButton(int orientacion) {
            return botonVacio();
        }

        @Override
        protected JButton createIncreaseButton(int orientacion) {
            return botonVacio();
        }

        private static JButton botonVacio() {
            JButton boton = new JButton();
            Dimension cero = new Dimension(0, 0);
            boton.setPreferredSize(cero);
            boton.setMinimumSize(cero);
            boton.setMaximumSize(cero);
            return boton;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle limites) {
            g.setColor(SUPERFICIE);
            g.fillRect(limites.x, limites.y, limites.width, limites.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle limites) {
            if (limites.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isDragging ? new Color(0x9AA5B4) : thumbColor);
            g2.fillRoundRect(limites.x + 2, limites.y + 2, limites.width - 4, limites.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            return new Dimension(12, 12);
        }
    }

    // ---------- Tablas ----------

    /** Tarjeta con título arriba, tabla con scroll al centro y un componente opcional abajo (puede ser null). */
    public static Tarjeta tarjetaConTabla(String titulo, JTable tabla, Component pie) {
        return tarjetaConTabla(subtitulo(titulo), tabla, pie);
    }

    public static Tarjeta tarjetaConTabla(Component encabezado, JTable tabla, Component pie) {
        estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(SUPERFICIE);
        scroll.getVerticalScrollBar().setUI(new ScrollTema());
        scroll.getHorizontalScrollBar().setUI(new ScrollTema());
        JPanel esquina = new JPanel();
        esquina.setBackground(ENCABEZADO);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, esquina);

        Tarjeta tarjeta = new Tarjeta(new BorderLayout(0, 14));
        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(scroll, BorderLayout.CENTER);
        if (pie != null) {
            tarjeta.add(pie, BorderLayout.SOUTH);
        }
        return tarjeta;
    }

    private static void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setFillsViewportHeight(true);
        tabla.setFont(fuente(Font.PLAIN, 13));
        tabla.setForeground(TEXTO);
        tabla.setBackground(SUPERFICIE);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO);
        tabla.setDefaultRenderer(Object.class, new Celda(SwingConstants.LEFT, null));

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setDefaultRenderer(new Encabezado(SwingConstants.LEFT));
    }

    /** Alinea a la derecha las columnas numéricas (celdas y encabezado). Llamar después de crear la tarjeta. */
    public static void alinearDerecha(JTable tabla, int... columnas) {
        for (int c : columnas) {
            TableColumn columna = tabla.getColumnModel().getColumn(c);
            columna.setCellRenderer(new Celda(SwingConstants.RIGHT, null));
            columna.setHeaderRenderer(new Encabezado(SwingConstants.RIGHT));
        }
    }

    /** Estilo común de las tablas de transacciones: monto a la derecha y tipo en verde (entra plata) o rojo (sale). */
    public static void formatoTransacciones(JTable tabla) {
        alinearDerecha(tabla, 3);
        tabla.getColumnModel().getColumn(2).setCellRenderer(new Celda(SwingConstants.LEFT, valor -> {
            if (valor instanceof TipoTransaccion tipo) {
                return switch (tipo) {
                    case DEPOSITO, TRANSFERENCIA_ENTRADA, INTERES -> EXITO;
                    case RETIRO, TRANSFERENCIA_SALIDA -> PELIGRO;
                };
            }
            return null;
        }));
    }

    /** Celda con relleno, filas alternas y color de texto opcional según el valor. Los enum se muestran "bonitos". */
    public static class Celda extends DefaultTableCellRenderer {
        private final Function<Object, Color> colorTexto;

        public Celda(int alineacion, Function<Object, Color> colorTexto) {
            this.colorTexto = colorTexto;
            setHorizontalAlignment(alineacion);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, false, fila, columna);
            if (valor instanceof Enum<?> e) {
                String nombre = e.name().toLowerCase().replace('_', ' ');
                setText(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1));
            }
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setFont(fuente(Font.PLAIN, 13));
            setForeground(TEXTO);
            setBackground(seleccionada ? SELECCION : (fila % 2 == 0 ? SUPERFICIE : FILA_ALTERNA));
            Color color = colorTexto == null ? null : colorTexto.apply(valor);
            if (color != null) {
                setForeground(color);
                setFont(fuente(Font.BOLD, 13));
            }
            return this;
        }
    }

    private static class Encabezado extends DefaultTableCellRenderer {
        Encabezado(int alineacion) {
            setHorizontalAlignment(alineacion);
            setOpaque(true);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 40);
        }

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
            setBackground(ENCABEZADO);
            setForeground(TEXTO_SUAVE);
            setFont(fuente(Font.BOLD, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            return this;
        }
    }
}
