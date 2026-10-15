package cooperativa.vista.tabla;

import cooperativa.modelo.cliente.Cliente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ModeloTablaClientes extends AbstractTableModel {
    private final String[] columnas = {"ID", "Nombre", "Teléfono"};
    private List<Cliente> clientes = new ArrayList<>();

    public void setClientes(List<Cliente> clientes) {
        this.clientes = new ArrayList<>(clientes);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return clientes.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length; // TODO M5: número de columnas
    }

    public String getValuAt(int fila, int columna) {
        return columnas[columna];
    }
    @Override
    public Object getValueAt(int fila, int columna) {
        Cliente cliente = clientes.get(fila);
        return switch (columna) {
            case 0 -> cliente.getId();
            case 1 -> cliente.getNombre();
            default -> cliente.getTelefono();
        };
    }
}
