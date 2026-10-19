package cooperativa.vista.tabla;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.vista.Formato;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Modelo de tabla de Cuenta. (M5 - Valeria) */
public class ModeloTablaCuentas extends AbstractTableModel {
    private final String[] columnas = {"Número", "Tipo", "Titular", "Saldo", "Estado"};
    private List<Cuenta> datos = new ArrayList<>();

    public void setDatos(List<Cuenta> datos) {
        this.datos = new ArrayList<>(datos);
        fireTableDataChanged();
    }

    /** Cuenta de una fila (para bloquear/desbloquear la fila seleccionada). */
    public Cuenta getCuenta(int fila) {
        return datos.get(fila);
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
        Cuenta cuenta = datos.get(fila);
        return switch (columna) {
            case 0 -> cuenta.getNumero();
            case 1 -> cuenta.getTipo();
            case 2 -> cuenta.getIdTitular();
            case 3 -> Formato.dinero(cuenta.getSaldo());
            default -> cuenta.isBloqueada() ? "Bloqueada" : "Activa";
        };
    }
}
