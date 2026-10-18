package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.vista.tabla.ModeloTablaClientes;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class PanelClientes extends JPanel {
    private final Banco banco;
    private final ModeloTablaClientes modelo = new ModeloTablaClientes();
    private final JTextField campoNombre = new JTextField(15);
    private final JTextField campoTelefono = new JTextField(10);

    public PanelClientes(Banco banco) {
        this.banco = banco;
        setLayout(new BorderLayout());

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(campoNombre);
        formulario.add(new JLabel("Telefono:"));
        formulario.add(campoTelefono);
        JButton botonCrear = new JButton("Crear Cliente");
        formulario.add(botonCrear);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        botonCrear.addActionListener(e -> crearCliente());
        refrescar();
    }

    private void crearCliente() {
        try {
            banco.crearCliente(campoNombre.getText().trim(), campoTelefono.getText().trim());
            campoNombre.setText("");
            campoTelefono.setText("");
            refrescar();

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescar() {
        modelo.setClientes(banco.listarClientes());
    }

}
