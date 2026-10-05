package cooperativa.repositorio.sqlite;

import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.repositorio.Repositorio;

import java.util.List;
import java.util.Optional;

/** Repositorio SQLite de Cuenta. (M2b capa 1 - Daniel; al leer usa CuentaFactory) */
public class RepositorioCuentaSQLite implements Repositorio<Cuenta> {
    @Override
    public void guardar(Cuenta entidad) {
        // TODO M2b
    }

    @Override
    public Optional<Cuenta> buscarPorId(String id) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public List<Cuenta> listar() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void eliminar(String id) {
        // TODO M2b
    }
}
