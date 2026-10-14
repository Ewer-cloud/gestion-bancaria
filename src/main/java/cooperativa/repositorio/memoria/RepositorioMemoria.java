package cooperativa.repositorio.memoria;

import cooperativa.repositorio.Repositorio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Repositorio genérico que guarda en un Map. Recibe una función para
 * extraer el id de la entidad (por ejemplo Cliente::getId).
 */
public class RepositorioMemoria<T> implements Repositorio<T> {
    private final Map<String, T> datos = new LinkedHashMap<>();
    private final Function<T, String> extractorId;

    public RepositorioMemoria(Function<T, String> extractorId) {
        this.extractorId = extractorId;
    }

    @Override
    public void guardar(T entidad) {
        datos.put(extractorId.apply(entidad), entidad);
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public void eliminar(String id) {
        datos.remove(id);
    }
}