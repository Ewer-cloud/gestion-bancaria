package cooperativa.vista.panel;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.servicio.Banco;
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
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
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
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);

        BotonTema botonDepositar = new BotonTema("Depositar", BotonTema.Estilo.PRIMARIO);
        botonDepositar.addActionListener(e -> ejecutar(this::depositar));
        BotonTema botonRetirar = new BotonTema("Retirar", BotonTema.Estilo.PRIMARIO);
        botonRetirar.addActionListener(e -> ejecutar(this::retirar));
        BotonTema botonTransferir = new BotonTema("Transferir", BotonTema.Estilo.PRIMARIO);
        botonTransferir.addActionListener(e -> ejecutar(this::transferir));

        JPanel formularios = new JPanel(new GridLayout(1, 3, 16, 0));
        formularios.setOpaque(false);
        formularios.add(formulario("Depositar", "Suma dinero a una cuenta",
                Tema.campo("Cuenta", cuentaDeposito), Tema.campo("Monto", montoDeposito), botonDepositar));
        formularios.add(formulario("Retirar", "Saca dinero de una cuenta",
                Tema.campo("Cuenta", cuentaRetiro), Tema.campo("Monto", montoRetiro), botonRetirar));
        formularios.add(formulario("Transferir", "Mueve dinero entre dos cuentas",
                Tema.campo("Origen", cuentaOrigen), Tema.campo("Destino", cuentaDestino),
                Tema.campo("Monto", montoTransferencia), botonTransferir));

        estado.setFont(Tema.fuente(Font.BOLD, 13));
        JTable tabla = new JTable(modeloTransacciones);
        Tarjeta movimientos = Tema.tarjetaConTabla("Últimos movimientos", tabla, estado);
        Tema.formatoTransacciones(tabla);

        add(formularios, BorderLayout.NORTH);
        add(movimientos, BorderLayout.CENTER);
        refrescar();
    }

    /** Tarjeta vertical: título, una línea de ayuda y los campos uno debajo del otro. */
    private static Tarjeta formulario(String titulo, String ayuda, Component... componentes) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.add(Tema.subtitulo(titulo));
        tarjeta.add(Box.createVerticalStrut(2));
        JLabel textoAyuda = Tema.textoSuave(ayuda);
        textoAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(textoAyuda);
        for (Component c : componentes) {
            tarjeta.add(Box.createVerticalStrut(12));
            tarjeta.add(c);
        }
        return tarjeta;
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
            estado.setForeground(Tema.EXITO);
            estado.setText("OK: " + mensaje);
            refrescar();
        } catch (CuentaBloqueadaException | SaldoInsuficienteException | RuntimeException ex) {
            estado.setForeground(Tema.PELIGRO);
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
