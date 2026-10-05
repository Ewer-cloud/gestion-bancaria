package cooperativa.vista.tabla;

import cooperativa.modelo.cuenta.Cuenta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Cuenta. (M5 - Valeria) */
public class ModeloTablaCuentas extends AbstractTableModel {
    private List<Cuenta> datos = new ArrayList<>();

    public void setDatos(List<Cuenta> datos) {
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
