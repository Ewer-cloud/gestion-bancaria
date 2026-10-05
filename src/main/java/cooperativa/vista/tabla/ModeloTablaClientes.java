package cooperativa.vista.tabla;

import cooperativa.modelo.cliente.Cliente;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Cliente. (M5 - Valeria) */
public class ModeloTablaClientes extends AbstractTableModel {
    private List<Cliente> datos = new ArrayList<>();

    public void setDatos(List<Cliente> datos) {
        this.datos = datos;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return 0; // TODO M5: número de columnas
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return null; // TODO M5
    }
}
