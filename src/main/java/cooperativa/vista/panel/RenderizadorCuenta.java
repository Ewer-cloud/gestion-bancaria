package cooperativa.vista.panel;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.vista.Formato;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/** Muestra una Cuenta de forma legible dentro de un JComboBox. (M5) */
class RenderizadorCuenta extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                  boolean seleccionado, boolean foco) {
        super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        if (valor instanceof Cuenta cuenta) {
            setText(cuenta.getNumero() + " · " + cuenta.getTipo() + " · " + Formato.dineroCorto(cuenta.getSaldo())
                    + (cuenta.isBloqueada() ? " · BLOQUEADA" : ""));
        }
        return this;
    }
}
