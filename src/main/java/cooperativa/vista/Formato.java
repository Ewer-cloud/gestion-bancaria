package cooperativa.vista;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Utilidades de formato para la vista. Solo valida el FORMATO de lo que escribe el cajero, no reglas de negocio. (M5) */
public final class Formato {
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Formato() {
    }

    /** Convierte el texto de un campo en monto. Lanza IllegalArgumentException con un mensaje claro si no es un número. */
    public static BigDecimal leerMonto(String texto, String nombreCampo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Escribe el " + nombreCampo);
        }
        try {
            return new BigDecimal(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El " + nombreCampo + " debe ser un número (ejemplo: 150000)");
        }
    }

    /** Igual que {@link #leerMonto} pero devuelve null si el campo está vacío (filtros opcionales). */
    public static BigDecimal leerMontoOpcional(String texto, String nombreCampo) {
        return (texto == null || texto.isBlank()) ? null : leerMonto(texto, nombreCampo);
    }

    public static String dinero(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public static String fechaHora(LocalDateTime fecha) {
        return FECHA_HORA.format(fecha);
    }
}
