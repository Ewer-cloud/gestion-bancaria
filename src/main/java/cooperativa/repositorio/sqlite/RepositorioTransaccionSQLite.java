package cooperativa.repositorio.sqlite;

import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.Repositorio;

import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Transaccion. (M2b capa 2 - Daniel L.) */
public class RepositorioTransaccionSQLite implements Repositorio<Transaccion> {
    @Override
    public void guardar(Transaccion entidad) {
        // TODO M2b
    }

    @Override
    public Optional<Transaccion> buscarPorId(String id) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public List<Transaccion> listar() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void eliminar(String id) {
        // TODO M2b
    }
}
