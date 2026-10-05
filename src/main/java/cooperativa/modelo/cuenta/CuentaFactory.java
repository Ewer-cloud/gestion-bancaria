package cooperativa.modelo.cuenta;

import java.math.BigDecimal;

/** Crea y reconstruye el tipo correcto de cuenta (Factory). (M1 - Daniel L.) */
public final class CuentaFactory {
    private CuentaFactory() {
    }

    /**
     * @param tipo "AHORRO" o "CORRIENTE" (igual que la columna tipo de la BD)
     */
    public static Cuenta crear(String tipo, String numero, String idTitular, BigDecimal saldo, boolean bloqueada) {
        // TODO M1: devolver CuentaAhorro o CuentaCorriente según el tipo; IllegalArgumentException si no existe
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
