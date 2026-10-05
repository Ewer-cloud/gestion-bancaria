package cooperativa.vista.tabla;

import cooperativa.servicio.alerta.Alerta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Alerta. (M5 - Valeria) */
public class ModeloTablaAlertas extends AbstractTableModel {
    private List<Alerta> datos = new ArrayList<>();

    public void setDatos(List<Alerta> datos) {
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
