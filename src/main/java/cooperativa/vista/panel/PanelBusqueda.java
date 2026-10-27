package cooperativa.vista.panel;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.servicio.Banco;
import cooperativa.servicio.busqueda.CriterioBusqueda;
import cooperativa.servicio.busqueda.CriterioCliente;
import cooperativa.servicio.busqueda.CriterioFecha;
import cooperativa.servicio.busqueda.CriterioMonto;
import cooperativa.servicio.busqueda.CriterioTipo;
import cooperativa.vista.BotonTema;
import cooperativa.vista.Formato;
import cooperativa.vista.Tarjeta;
import cooperativa.vista.Tema;
import cooperativa.vista.tabla.ModeloTablaTransacciones;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Campos de filtro (monto, tipo, cliente, fecha) y tabla de resultados. Los filtros se combinan con AND. (M5 - Valeria) */
public class PanelBusqueda extends JPanel implements Refrescable {
    private static final String TODOS = "(Todos)";

    private final Banco banco;
    private final ModeloTablaTransacciones modelo = new ModeloTablaTransacciones();
    private final JTextField montoMinimo = new JTextField(8);
    private final JTextField montoMaximo = new JTextField(8);
    private final JTextField fechaDesde = new JTextField(9);
    private final JTextField fechaHasta = new JTextField(9);
    private final JComboBox<Object> comboTipo = new JComboBox<>();
    private final JComboBox<Object> comboCliente = new JComboBox<>();
    private final JLabel resumen = new JLabel(" ");

    public PanelBusqueda(Banco banco) {
        this.banco = banco;
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);

        comboTipo.addItem(TODOS);
        for (TipoTransaccion t : TipoTransaccion.values()) {
            comboTipo.addItem(t);
        }

        comboCliente.setPreferredSize(new Dimension(270, 38)); // cabe "CLI-001 - Nombre (teléfono)"
        BotonTema botonBuscar = new BotonTema("Buscar", BotonTema.Estilo.PRIMARIO);
        BotonTema botonLimpiar = new BotonTema("Limpiar filtros", BotonTema.Estilo.SECUNDARIO);

        // Dos filas para que todo quepa sin cortarse: montos/tipo/cliente y fechas/botones.
        Tarjeta filtros = new Tarjeta();
        filtros.setLayout(new BoxLayout(filtros, BoxLayout.Y_AXIS));
        filtros.add(Tema.subtitulo("Filtros"));
        filtros.add(Box.createVerticalStrut(14));
        filtros.add(Tema.fila(
                Tema.campo("Monto desde", montoMinimo),
                Tema.campo("Monto hasta", montoMaximo),
                Tema.campo("Tipo", comboTipo),
                Tema.campo("Cliente", comboCliente)));
        filtros.add(Box.createVerticalStrut(12));
        filtros.add(Tema.fila(
                Tema.campo("Fecha desde (AAAA-MM-DD)", fechaDesde),
                Tema.campo("Fecha hasta (AAAA-MM-DD)", fechaHasta),
                Tema.campo(null, botonBuscar),
                Tema.campo(null, botonLimpiar)));

        resumen.setFont(Tema.fuente(Font.BOLD, 13));
        resumen.setForeground(Tema.TEXTO_SUAVE);
        JTable tabla = new JTable(modelo);
        Tarjeta resultados = Tema.tarjetaConTabla("Resultados", tabla, resumen);
        Tema.formatoTransacciones(tabla);

        add(filtros, BorderLayout.NORTH);
        add(resultados, BorderLayout.CENTER);

        botonBuscar.addActionListener(e -> buscar(true));
        botonLimpiar.addActionListener(e -> limpiar());
        refrescar();
    }

    /** Lee los campos, arma los criterios y le pide la búsqueda a Banco. */
    private void buscar(boolean mostrarErrores) {
        try {
            List<CriterioBusqueda> criterios = new ArrayList<>();

            BigDecimal minimo = Formato.leerMontoOpcional(montoMinimo.getText(), "monto mínimo");
            BigDecimal maximo = Formato.leerMontoOpcional(montoMaximo.getText(), "monto máximo");
            if (minimo != null || maximo != null) {
                criterios.add(new CriterioMonto(minimo, maximo));
            }
            if (comboTipo.getSelectedItem() instanceof TipoTransaccion tipo) {
                criterios.add(new CriterioTipo(tipo));
            }
            if (comboCliente.getSelectedItem() instanceof Cliente cliente) {
                Set<String> cuentas = banco.listarCuentasDeCliente(cliente.getId()).stream()
                        .map(Cuenta::getNumero)
                        .collect(Collectors.toSet());
                criterios.add(new CriterioCliente(cuentas));
            }
            LocalDate desde = leerFecha(fechaDesde.getText(), "desde");
            LocalDate hasta = leerFecha(fechaHasta.getText(), "hasta");
            if (desde != null || hasta != null) {
                LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
                LocalDateTime fin = hasta == null ? null : hasta.atTime(LocalTime.MAX);
                criterios.add(new CriterioFecha(inicio, fin));
            }

            List<Transaccion> resultado = banco.buscarTransacciones(criterios.toArray(new CriterioBusqueda[0]));
            modelo.setDatos(resultado);
            resumen.setText(resultado.size() + " transacción(es) encontrada(s)");
        } catch (RuntimeException ex) {
            if (mostrarErrores) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en la búsqueda", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static LocalDate leerFecha(String texto, String nombre) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha " + nombre + " debe tener formato AAAA-MM-DD (ejemplo: 2026-10-28)");
        }
    }

    private void limpiar() {
        montoMinimo.setText("");
        montoMaximo.setText("");
        fechaDesde.setText("");
        fechaHasta.setText("");
        comboTipo.setSelectedIndex(0);
        comboCliente.setSelectedIndex(0);
        buscar(true);
    }

    @Override
    public void refrescar() {
        Object anterior = comboCliente.getSelectedItem();
        comboCliente.removeAllItems();
        comboCliente.addItem(TODOS);
        for (Cliente c : banco.listarClientes()) {
            comboCliente.addItem(c);
        }
        if (anterior != null) {
            comboCliente.setSelectedItem(anterior);
        }
        buscar(false);
    }
}
