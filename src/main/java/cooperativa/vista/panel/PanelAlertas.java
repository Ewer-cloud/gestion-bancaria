package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.AlertaListener;
import cooperativa.servicio.alerta.NivelRiesgo;
import cooperativa.vista.BotonTema;
import cooperativa.vista.Tema;
import cooperativa.vista.tabla.ModeloTablaAlertas;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/** Lista las alertas de fraude con su nivel de riesgo. Es el Observer: se registra en el DetectorFraude. (M5 - Valeria) */
public class PanelAlertas extends JPanel implements AlertaListener {
    private final ModeloTablaAlertas modelo = new ModeloTablaAlertas();
    private final JLabel contador = new JLabel("Sin alertas");

    public PanelAlertas(Banco banco) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JTable tabla = new JTable(modelo);
        BotonTema botonLimpiar = new BotonTema("Limpiar lista", BotonTema.Estilo.SECUNDARIO);
        botonLimpiar.addActionListener(e -> {
            modelo.setDatos(List.of());
            contador.setText("Sin alertas");
        });

        contador.setFont(Tema.fuente(Font.BOLD, 13));
        contador.setForeground(Tema.TEXTO_SUAVE);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        controles.setOpaque(false);
        controles.add(contador);
        controles.add(botonLimpiar);
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(Tema.subtitulo("Alertas de fraude"), BorderLayout.WEST);
        encabezado.add(controles, BorderLayout.EAST);

        add(Tema.tarjetaConTabla(encabezado, tabla, null), BorderLayout.CENTER);
        tabla.getColumnModel().getColumn(1).setCellRenderer(new Tema.Celda(SwingConstants.LEFT, valor ->
                valor instanceof NivelRiesgo nivel ? colorDe(nivel) : null));
        tabla.getColumnModel().getColumn(0).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(480);

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

    /** Color del nivel de riesgo: ALTO rojo, MEDIO naranja, BAJO verde. */
    private static Color colorDe(NivelRiesgo nivel) {
        return switch (nivel) {
            case ALTO -> Tema.PELIGRO;
            case MEDIO -> Tema.AVISO;
            case BAJO -> Tema.EXITO;
        };
    }
}
