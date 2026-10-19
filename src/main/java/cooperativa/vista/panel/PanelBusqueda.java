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
import cooperativa.vista.Formato;
import cooperativa.vista.tabla.ModeloTablaTransacciones;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
        setLayout(new BorderLayout());

        comboTipo.addItem(TODOS);
        for (TipoTransaccion t : TipoTransaccion.values()) {
            comboTipo.addItem(t);
        }

        // Dos filas para que todo quepa sin cortarse: montos/tipo/cliente y fechas/botones.
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Monto desde:"));
        fila1.add(montoMinimo);
        fila1.add(new JLabel("hasta:"));
        fila1.add(montoMaximo);
        fila1.add(new JLabel("Tipo:"));
        fila1.add(comboTipo);
        fila1.add(new JLabel("Cliente:"));
        fila1.add(comboCliente);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Fecha desde (AAAA-MM-DD):"));
        fila2.add(fechaDesde);
        fila2.add(new JLabel("hasta:"));
        fila2.add(fechaHasta);
        JButton botonBuscar = new JButton("Buscar");
        JButton botonLimpiar = new JButton("Limpiar filtros");
        fila2.add(botonBuscar);
        fila2.add(botonLimpiar);

        JPanel filtros = new JPanel();
        filtros.setLayout(new BoxLayout(filtros, BoxLayout.Y_AXIS));
        filtros.add(fila1);
        filtros.add(fila2);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        add(resumen, BorderLayout.SOUTH);

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
