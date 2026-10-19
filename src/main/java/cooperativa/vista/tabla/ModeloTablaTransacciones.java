package cooperativa.vista.tabla;

import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.vista.Formato;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Transaccion. (M5 - Valeria) */
public class ModeloTablaTransacciones extends AbstractTableModel {
    private final String[] columnas = {"ID", "Cuenta", "Tipo", "Monto", "Fecha"};
    private List<Transaccion> datos = new ArrayList<>();

    public void setDatos(List<Transaccion> datos) {
        this.datos = new ArrayList<>(datos);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Transaccion t = datos.get(fila);
        return switch (columna) {
            case 0 -> t.getId();
            case 1 -> t.getNumeroCuenta();
            case 2 -> t.getTipo();
            case 3 -> Formato.dinero(t.getMonto());
            default -> Formato.fechaHora(t.getFecha());
        };
    }
}
