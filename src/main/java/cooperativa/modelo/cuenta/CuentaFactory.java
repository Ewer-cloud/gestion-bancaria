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
            if (tipo == null) {
                throw new IllegalArgumentException("El tipo de cuenta no puede ser nulo");
            }
            switch (tipo) {
                case "AHORRO":
                    return new CuentaAhorro(numero, idTitular, saldo, bloqueada);
                case "CORRIENTE":
                    return new CuentaCorriente(numero, idTitular, saldo, bloqueada);
                default:
                    throw new IllegalArgumentException("Tipo de cuenta desconocido: " + tipo);
            }
        }
    }
