package cooperativa.vista.panel;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.servicio.Banco;
import cooperativa.vista.Formato;
import cooperativa.vista.tabla.ModeloTablaTransacciones;

import javax.swing.BorderFactory;
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
import java.awt.Color;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Depositar, retirar y transferir. Muestra los errores que lanza el servicio. (M5 - Valeria) */
public class PanelOperaciones extends JPanel implements Refrescable {
    /** Una operación del formulario: lee los campos, llama a Banco y devuelve el mensaje de éxito. */
    private interface Operacion {
        String ejecutar() throws CuentaBloqueadaException, SaldoInsuficienteException;
    }

    private final Banco banco;
    private final ModeloTablaTransacciones modeloTransacciones = new ModeloTablaTransacciones();
    private final JComboBox<Cuenta> cuentaDeposito = crearComboCuentas();
    private final JComboBox<Cuenta> cuentaRetiro = crearComboCuentas();
    private final JComboBox<Cuenta> cuentaOrigen = crearComboCuentas();
    private final JComboBox<Cuenta> cuentaDestino = crearComboCuentas();
    private final JTextField montoDeposito = new JTextField(10);
    private final JTextField montoRetiro = new JTextField(10);
    private final JTextField montoTransferencia = new JTextField(10);
    private final JLabel estado = new JLabel(" ");

    public PanelOperaciones(Banco banco) {
        this.banco = banco;
        setLayout(new BorderLayout());

        JPanel formularios = new JPanel();
        formularios.setLayout(new BoxLayout(formularios, BoxLayout.Y_AXIS));
        formularios.add(fila("Depositar", "Cuenta:", cuentaDeposito, montoDeposito, "Depositar", this::depositar));
        formularios.add(fila("Retirar", "Cuenta:", cuentaRetiro, montoRetiro, "Retirar", this::retirar));
        formularios.add(filaTransferencia());

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.setBorder(BorderFactory.createTitledBorder("Últimos movimientos"));
        abajo.add(new JScrollPane(new JTable(modeloTransacciones)), BorderLayout.CENTER);
        abajo.add(estado, BorderLayout.SOUTH);

        add(formularios, BorderLayout.NORTH);
        add(abajo, BorderLayout.CENTER);
        refrescar();
    }

    private JPanel fila(String titulo, String etiqueta, JComboBox<Cuenta> combo, JTextField monto,
                        String textoBoton, Operacion operacion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(new JLabel(etiqueta));
        panel.add(combo);
        panel.add(new JLabel("Monto:"));
        panel.add(monto);
        JButton boton = new JButton(textoBoton);
        boton.addActionListener(e -> ejecutar(operacion));
        panel.add(boton);
        return panel;
    }

    private JPanel filaTransferencia() {
        JPanel origenYDestino = new JPanel(new FlowLayout(FlowLayout.LEFT));
        origenYDestino.add(new JLabel("Origen:"));
        origenYDestino.add(cuentaOrigen);
        origenYDestino.add(new JLabel("Destino:"));
        origenYDestino.add(cuentaDestino);

        JPanel montoYBoton = new JPanel(new FlowLayout(FlowLayout.LEFT));
        montoYBoton.add(new JLabel("Monto:"));
        montoYBoton.add(montoTransferencia);
        JButton boton = new JButton("Transferir");
        boton.addActionListener(e -> ejecutar(this::transferir));
        montoYBoton.add(boton);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Transferir"));
        panel.add(origenYDestino);
        panel.add(montoYBoton);
        return panel;
    }

    private String depositar() throws CuentaBloqueadaException {
        Cuenta cuenta = elegida(cuentaDeposito, "la cuenta");
        BigDecimal monto = Formato.leerMonto(montoDeposito.getText(), "monto");
        banco.depositar(cuenta.getNumero(), monto);
        montoDeposito.setText("");
        return "Depósito de " + Formato.dinero(monto) + " en " + cuenta.getNumero();
    }

    private String retirar() throws CuentaBloqueadaException, SaldoInsuficienteException {
        Cuenta cuenta = elegida(cuentaRetiro, "la cuenta");
        BigDecimal monto = Formato.leerMonto(montoRetiro.getText(), "monto");
        banco.retirar(cuenta.getNumero(), monto);
        montoRetiro.setText("");
        return "Retiro de " + Formato.dinero(monto) + " de " + cuenta.getNumero();
    }

    private String transferir() throws CuentaBloqueadaException, SaldoInsuficienteException {
        Cuenta origen = elegida(cuentaOrigen, "la cuenta de origen");
        Cuenta destino = elegida(cuentaDestino, "la cuenta de destino");
        BigDecimal monto = Formato.leerMonto(montoTransferencia.getText(), "monto");
        banco.transferir(origen.getNumero(), destino.getNumero(), monto);
        montoTransferencia.setText("");
        return "Transferencia de " + Formato.dinero(monto) + " de " + origen.getNumero() + " a " + destino.getNumero();
    }

    /** Cada botón: ejecuta la operación y muestra el resultado o el error. Sin lógica de negocio aquí. */
    private void ejecutar(Operacion operacion) {
        try {
            String mensaje = operacion.ejecutar();
            estado.setForeground(new Color(0, 110, 0));
            estado.setText("OK: " + mensaje);
            refrescar();
        } catch (CuentaBloqueadaException | SaldoInsuficienteException | RuntimeException ex) {
            estado.setForeground(Color.RED.darker());
            estado.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo realizar la operación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Cuenta elegida(JComboBox<Cuenta> combo, String nombre) {
        Cuenta cuenta = (Cuenta) combo.getSelectedItem();
        if (cuenta == null) {
            throw new IllegalArgumentException("Selecciona " + nombre);
        }
        return cuenta;
    }

    private static JComboBox<Cuenta> crearComboCuentas() {
        JComboBox<Cuenta> combo = new JComboBox<>();
        combo.setRenderer(new RenderizadorCuenta());
        return combo;
    }

    private static void llenar(JComboBox<Cuenta> combo, List<Cuenta> cuentas) {
        Cuenta anterior = (Cuenta) combo.getSelectedItem();
        combo.removeAllItems();
        for (Cuenta c : cuentas) {
            combo.addItem(c);
        }
        if (anterior != null) {
            combo.setSelectedItem(anterior); // Cuenta.equals compara por número
        }
    }

    @Override
    public void refrescar() {
        List<Cuenta> cuentas = banco.listarCuentas();
        llenar(cuentaDeposito, cuentas);
        llenar(cuentaRetiro, cuentas);
        llenar(cuentaOrigen, cuentas);
        llenar(cuentaDestino, cuentas);
        List<Transaccion> recientes = new ArrayList<>(banco.listarTransacciones());
        Collections.reverse(recientes);
        modeloTransacciones.setDatos(recientes);
    }
}
