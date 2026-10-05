package cooperativa.modelo.cuenta;

import java.math.BigDecimal;

/** Cuentas que generan intereses en el cierre de mes. (M1 - Daniel L.) */
public interface GeneradoraDeIntereses {
    BigDecimal calcularInteres();

    void aplicarInteres();
}
