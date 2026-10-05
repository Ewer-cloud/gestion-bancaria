package cooperativa.modelo.transaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Movimiento de una cuenta. Inmutable: sin setters. (M2 - Daniel) */
public class Transaccion {
    private final String id;
    private final String numeroCuenta;
    private final TipoTransaccion tipo;
    private final BigDecimal monto;
    private final LocalDateTime fecha;

    public Transaccion(String id, String numeroCuenta, TipoTransaccion tipo, BigDecimal monto, LocalDateTime fecha) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    // TODO M2: equals y hashCode por id, toString
}
