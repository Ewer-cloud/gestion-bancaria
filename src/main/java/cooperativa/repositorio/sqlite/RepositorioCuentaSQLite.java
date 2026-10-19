package cooperativa.repositorio.sqlite;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.cuenta.CuentaFactory;
import cooperativa.modelo.excepcion.RepositorioException;
import cooperativa.repositorio.Repositorio;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Cuenta. (M2b capa 1 - Daniel; al leer usa CuentaFactory) */
public class RepositorioCuentaSQLite implements Repositorio<Cuenta> {
    private static final String COLUMNAS = "numero, tipo, saldo, id_titular, bloqueada";

    // UPSERT y no "INSERT OR REPLACE": REPLACE borra la fila y rompería las llaves foráneas de transaccion.
    private static final String UPSERT = """
            INSERT INTO cuenta (numero, tipo, saldo, id_titular, bloqueada) VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(numero) DO UPDATE SET tipo = excluded.tipo, saldo = excluded.saldo,
                id_titular = excluded.id_titular, bloqueada = excluded.bloqueada""";

    @Override
    public void guardar(Cuenta entidad) {
        try (Connection c = ConexionDB.obtener(); PreparedStatement ps = c.prepareStatement(UPSERT)) {
            ps.setString(1, entidad.getNumero());
            ps.setString(2, entidad.getTipo());
            ps.setString(3, entidad.getSaldo().toPlainString()); // el dinero se guarda como texto
            ps.setString(4, entidad.getIdTitular());
            ps.setInt(5, entidad.isBloqueada() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo guardar la cuenta " + entidad.getNumero(), e);
        }
    }

    @Override
    public Optional<Cuenta> buscarPorId(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNAS + " FROM cuenta WHERE numero = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(aCuenta(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar la cuenta " + id, e);
        }
    }

    @Override
    public List<Cuenta> listar() {
        List<Cuenta> resultado = new ArrayList<>();
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNAS + " FROM cuenta ORDER BY rowid");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(aCuenta(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudieron listar las cuentas", e);
        }
    }

    @Override
    public void eliminar(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cuenta WHERE numero = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo eliminar la cuenta " + id, e);
        }
    }

    /** Reconstruye la cuenta del tipo correcto según la columna tipo (Factory). */
    private static Cuenta aCuenta(ResultSet rs) throws SQLException {
        return CuentaFactory.crear(
                rs.getString("tipo"),
                rs.getString("numero"),
                rs.getString("id_titular"),
                new BigDecimal(rs.getString("saldo")),
                rs.getInt("bloqueada") == 1);
    }
}
