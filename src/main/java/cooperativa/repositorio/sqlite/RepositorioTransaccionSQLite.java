package cooperativa.repositorio.sqlite;

import cooperativa.modelo.excepcion.RepositorioException;
import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.Repositorio;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Transaccion. (M2b capa 2 - Daniel L.) */
public class RepositorioTransaccionSQLite implements Repositorio<Transaccion> {
    private static final String COLUMNAS = "id, numero_cuenta, tipo, monto, fecha";
    private static final String UPSERT = """
            INSERT INTO transaccion (id, numero_cuenta, tipo, monto, fecha) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET numero_cuenta = excluded.numero_cuenta, tipo = excluded.tipo,
                monto = excluded.monto, fecha = excluded.fecha""";

    @Override
    public void guardar(Transaccion entidad) {
        try (Connection c = ConexionDB.obtener(); PreparedStatement ps = c.prepareStatement(UPSERT)) {
            ps.setString(1, entidad.getId());
            ps.setString(2, entidad.getNumeroCuenta());
            ps.setString(3, entidad.getTipo().name());
            ps.setString(4, entidad.getMonto().toPlainString());
            ps.setString(5, entidad.getFecha().toString()); // ISO-8601
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo guardar la transacción " + entidad.getId(), e);
        }
    }

    @Override
    public Optional<Transaccion> buscarPorId(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNAS + " FROM transaccion WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(aTransaccion(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar la transacción " + id, e);
        }
    }

    @Override
    public List<Transaccion> listar() {
        List<Transaccion> resultado = new ArrayList<>();
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNAS + " FROM transaccion ORDER BY rowid");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(aTransaccion(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudieron listar las transacciones", e);
        }
    }

    @Override
    public void eliminar(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM transaccion WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo eliminar la transacción " + id, e);
        }
    }

    private static Transaccion aTransaccion(ResultSet rs) throws SQLException {
        return new Transaccion(
                rs.getString("id"),
                rs.getString("numero_cuenta"),
                TipoTransaccion.valueOf(rs.getString("tipo")),
                new BigDecimal(rs.getString("monto")),
                LocalDateTime.parse(rs.getString("fecha")));
    }
}
