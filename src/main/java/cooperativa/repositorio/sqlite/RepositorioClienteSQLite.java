package cooperativa.repositorio.sqlite;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.repositorio.Repositorio;

import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Cliente. (M2b capa 2 - Daniel L.) */
public class RepositorioClienteSQLite implements Repositorio<Cliente> {
    @Override
    public void guardar(Cliente entidad) {
        // TODO M2b
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public List<Cliente> listar() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void eliminar(String id) {
        // TODO M2b
    }
}
