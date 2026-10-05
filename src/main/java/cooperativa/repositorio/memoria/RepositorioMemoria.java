package cooperativa.repositorio.memoria;

import cooperativa.repositorio.Repositorio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Repositorio en memoria. Recibe una función que extrae el id (por ejemplo Cliente::getId).
 * (M2 - Daniel)
 */
public class RepositorioMemoria<T> implements Repositorio<T> {
    private final Map<String, T> datos = new LinkedHashMap<>();
    private final Function<T, String> extractorId;

    public RepositorioMemoria(Function<T, String> extractorId) {
        this.extractorId = extractorId;
    }

    @Override
    public void guardar(T entidad) {
        // TODO M2
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public List<T> listar() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void eliminar(String id) {
        // TODO M2
    }
}
