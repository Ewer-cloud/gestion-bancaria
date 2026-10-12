package cooperativa.modelo.transaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Movimiento inmutable sobre una cuenta. Sin setters. */
public class Transaccion {
    private final String id;
    private final String numeroCuenta;
    private final TipoTransaccion tipo;
    private final BigDecimal monto;
    private final LocalDateTime fecha;

    public Transaccion(String id, String numeroCuenta, TipoTransaccion tipo,
                       BigDecimal monto, LocalDateTime fecha) {
        this.id = Objects.requireNonNull(id, "id");
        this.numeroCuenta = Objects.requireNonNull(numeroCuenta, "numeroCuenta");
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.monto = Objects.requireNonNull(monto, "monto");
        this.fecha = Objects.requireNonNull(fecha, "fecha");
    }

    public String getId() { return id; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public TipoTransaccion getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaccion otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return id + " | " + tipo + " | cuenta " + numeroCuenta + " | " + monto + " | " + fecha;
    }
}