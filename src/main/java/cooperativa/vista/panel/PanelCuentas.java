package cooperativa.vista.panel;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.servicio.Banco;
import cooperativa.vista.BotonTema;
import cooperativa.vista.Tarjeta;
import cooperativa.vista.Tema;
import cooperativa.vista.tabla.ModeloTablaCuentas;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);

        comboClientes.setPreferredSize(new Dimension(300, 38)); // cabe "CLI-001 - Nombre (teléfono)"
        BotonTema botonAbrir = new BotonTema("Abrir cuenta", BotonTema.Estilo.PRIMARIO);
        Tarjeta arriba = new Tarjeta(new BorderLayout(0, 14));
        arriba.add(Tema.subtitulo("Abrir una cuenta"), BorderLayout.NORTH);
        arriba.add(Tema.fila(
                Tema.campo("Cliente", comboClientes),
                Tema.campo("Tipo de cuenta", comboTipo),
                Tema.campo(null, botonAbrir)), BorderLayout.CENTER);

        BotonTema botonBloquear = new BotonTema("Bloquear seleccionada", BotonTema.Estilo.PELIGRO);
        BotonTema botonDesbloquear = new BotonTema("Desbloquear seleccionada", BotonTema.Estilo.SECUNDARIO);
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        abajo.setOpaque(false);
        abajo.add(botonBloquear);
        abajo.add(Box.createHorizontalStrut(10));
        abajo.add(botonDesbloquear);

        add(arriba, BorderLayout.NORTH);
        add(Tema.tarjetaConTabla("Cuentas del cliente", tabla, abajo), BorderLayout.CENTER);
        Tema.alinearDerecha(tabla, 3);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new Tema.Celda(SwingConstants.LEFT,
                estado -> "Bloqueada".equals(estado) ? Tema.PELIGRO : Tema.EXITO));

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
