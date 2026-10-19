package cooperativa.vista.panel;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.servicio.Banco;
import cooperativa.vista.tabla.ModeloTablaCuentas;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/** Abrir cuentas, ver las cuentas de un cliente, bloquear y desbloquear. (M5 - Valeria) */
public class PanelCuentas extends JPanel implements Refrescable {
    private final Banco banco;
    private final ModeloTablaCuentas modelo = new ModeloTablaCuentas();
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<Cliente> comboClientes = new JComboBox<>();
    private final JComboBox<String> comboTipo = new JComboBox<>(new String[]{"AHORRO", "CORRIENTE"});
    private boolean actualizando = false;

    public PanelCuentas(Banco banco) {
        this.banco = banco;
        setLayout(new BorderLayout());

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.add(new JLabel("Cliente:"));
        arriba.add(comboClientes);
        arriba.add(new JLabel("Tipo:"));
        arriba.add(comboTipo);
        JButton botonAbrir = new JButton("Abrir cuenta");
        arriba.add(botonAbrir);

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonBloquear = new JButton("Bloquear seleccionada");
        JButton botonDesbloquear = new JButton("Desbloquear seleccionada");
        abajo.add(botonBloquear);
        abajo.add(botonDesbloquear);

        add(arriba, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        comboClientes.addActionListener(e -> {
            if (!actualizando) {
                mostrarCuentasDelCliente();
            }
        });
        botonAbrir.addActionListener(e -> abrirCuenta());
        botonBloquear.addActionListener(e -> cambiarBloqueo(true));
        botonDesbloquear.addActionListener(e -> cambiarBloqueo(false));
        refrescar();
    }

    private void abrirCuenta() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        if (cliente == null) {
            mostrarError("Primero crea un cliente en la pestaña Clientes");
            return;
        }
        try {
            banco.abrirCuenta(cliente.getId(), (String) comboTipo.getSelectedItem());
            mostrarCuentasDelCliente();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void cambiarBloqueo(boolean bloquear) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Selecciona una cuenta de la tabla");
            return;
        }
        String numero = modelo.getCuenta(fila).getNumero();
        try {
            if (bloquear) {
                banco.bloquear(numero);
            } else {
                banco.desbloquear(numero);
            }
            mostrarCuentasDelCliente();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarCuentasDelCliente() {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        modelo.setDatos(cliente == null ? List.of() : banco.listarCuentasDeCliente(cliente.getId()));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void refrescar() {
        actualizando = true;
        try {
            Cliente anterior = (Cliente) comboClientes.getSelectedItem();
            comboClientes.removeAllItems();
            for (Cliente c : banco.listarClientes()) {
                comboClientes.addItem(c);
            }
            if (anterior != null) {
                comboClientes.setSelectedItem(anterior); // Cliente.equals compara por id
            }
        } finally {
            actualizando = false;
        }
        mostrarCuentasDelCliente();
    }
}
