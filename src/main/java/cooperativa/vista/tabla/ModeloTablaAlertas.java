package cooperativa.vista.tabla;

import cooperativa.servicio.alerta.Alerta;
import cooperativa.vista.Formato;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Alerta. (M5 - Valeria) */
public class ModeloTablaAlertas extends AbstractTableModel {
    private final String[] columnas = {"Fecha", "Riesgo", "Cuenta", "Mensaje"};
    private List<Alerta> datos = new ArrayList<>();

    public void setDatos(List<Alerta> datos) {
        this.datos = new ArrayList<>(datos);
        fireTableDataChanged();
    }

    /** Agrega una alerta nueva arriba de la lista (la más reciente primero). */
    public void agregar(Alerta alerta) {
        datos.add(0, alerta);
        fireTableRowsInserted(0, 0);
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
        Alerta a = datos.get(fila);
        return switch (columna) {
            case 0 -> Formato.fechaHora(a.getFecha());
            case 1 -> a.getNivel();
            case 2 -> a.getTransaccion().getNumeroCuenta();
            default -> a.getMensaje();
        };
    }
}
