package cooperativa.vista.tabla;

import cooperativa.modelo.transaccion.Transaccion;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Transaccion. (M5 - Valeria) */
public class ModeloTablaTransacciones extends AbstractTableModel {
    private List<Transaccion> datos = new ArrayList<>();

    public void setDatos(List<Transaccion> datos) {
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
