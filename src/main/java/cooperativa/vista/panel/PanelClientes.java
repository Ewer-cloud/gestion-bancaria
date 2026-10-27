package cooperativa.vista.panel;

import cooperativa.servicio.Banco;
import cooperativa.vista.BotonTema;
import cooperativa.vista.Tarjeta;
import cooperativa.vista.Tema;
import cooperativa.vista.tabla.ModeloTablaClientes;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;

/** Formulario para crear clientes y tabla de clientes. (M5 - Valeria) */
public class PanelClientes extends JPanel implements Refrescable {
    private final Banco banco;
    private final ModeloTablaClientes modelo = new ModeloTablaClientes();
    private final JTextField campoNombre = new JTextField(20);
    private final JTextField campoTelefono = new JTextField(12);

    public PanelClientes(Banco banco) {
        this.banco = banco;
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);

        BotonTema botonCrear = new BotonTema("Crear cliente", BotonTema.Estilo.PRIMARIO);
        Tarjeta formulario = new Tarjeta(new BorderLayout(0, 14));
        formulario.add(Tema.subtitulo("Nuevo cliente"), BorderLayout.NORTH);
        formulario.add(Tema.fila(
                Tema.campo("Nombre", campoNombre),
                Tema.campo("Teléfono", campoTelefono),
                Tema.campo(null, botonCrear)), BorderLayout.CENTER);

        add(formulario, BorderLayout.NORTH);
        add(Tema.tarjetaConTabla("Clientes registrados", new JTable(modelo), null), BorderLayout.CENTER);

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

    @Override
    public void refrescar() {
        modelo.setClientes(banco.listarClientes());
    }
}
