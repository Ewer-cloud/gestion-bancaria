package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.AlertaListener;
import cooperativa.servicio.alerta.NivelRiesgo;
import cooperativa.vista.tabla.ModeloTablaAlertas;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

/** Lista las alertas de fraude con su nivel de riesgo. Es el Observer: se registra en el DetectorFraude. (M5 - Valeria) */
public class PanelAlertas extends JPanel implements AlertaListener {
    private final ModeloTablaAlertas modelo = new ModeloTablaAlertas();
    private final JLabel contador = new JLabel("Sin alertas");

    public PanelAlertas(Banco banco) {
        setLayout(new BorderLayout());

        JTable tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new RenderizadorRiesgo());
        tabla.getColumnModel().getColumn(0).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(520);

        JButton botonLimpiar = new JButton("Limpiar lista");
        botonLimpiar.addActionListener(e -> {
            modelo.setDatos(List.of());
            contador.setText("Sin alertas");
        });
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(contador);
        arriba.add(botonLimpiar);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        banco.getDetector().registrarListener(this);
    }

    /** El detector puede avisar desde cualquier hilo: se pinta siempre en el hilo de Swing. */
    @Override
    public void onAlerta(Alerta alerta) {
        SwingUtilities.invokeLater(() -> {
            modelo.agregar(alerta);
            contador.setText(modelo.getRowCount() + " alerta(s)");
        });
    }

    /** Pinta el nivel de riesgo: ALTO rojo, MEDIO naranja, BAJO verde. */
    private static class RenderizadorRiesgo extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            if (!seleccionada && valor instanceof NivelRiesgo nivel) {
                setForeground(switch (nivel) {
                    case ALTO -> new Color(180, 0, 0);
                    case MEDIO -> new Color(200, 110, 0);
                    case BAJO -> new Color(0, 130, 0);
                });
            }
            return this;
        }
    }
}
