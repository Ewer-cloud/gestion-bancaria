package cooperativa.repositorio.sqlite;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.excepcion.RepositorioException;
import cooperativa.repositorio.Repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Cliente. (M2b capa 2 - Daniel L.) */
public class RepositorioClienteSQLite implements Repositorio<Cliente> {
    private static final String UPSERT = """
            INSERT INTO cliente (id, nombre, telefono) VALUES (?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET nombre = excluded.nombre, telefono = excluded.telefono""";

    @Override
    public void guardar(Cliente entidad) {
        try (Connection c = ConexionDB.obtener(); PreparedStatement ps = c.prepareStatement(UPSERT)) {
            ps.setString(1, entidad.getId());
            ps.setString(2, entidad.getNombre());
            ps.setString(3, entidad.getTelefono());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo guardar el cliente " + entidad.getId(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre, telefono FROM cliente WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(aCliente(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar el cliente " + id, e);
        }
    }

    @Override
    public List<Cliente> listar() {
        List<Cliente> resultado = new ArrayList<>();
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre, telefono FROM cliente ORDER BY rowid");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(aCliente(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new RepositorioException("No se pudieron listar los clientes", e);
        }
    }

    @Override
    public void eliminar(String id) {
        try (Connection c = ConexionDB.obtener();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo eliminar el cliente " + id, e);
        }
    }

    private static Cliente aCliente(ResultSet rs) throws SQLException {
        return new Cliente(rs.getString("id"), rs.getString("nombre"), rs.getString("telefono"));
    }
}
