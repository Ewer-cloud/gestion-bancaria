package cooperativa.repositorio;

import java.util.List;
import java.util.Optional;

/** Contrato genérico de almacenamiento. Lo implementan memoria y SQLite. (M2 - Daniel) */
public interface Repositorio<T> {
    void guardar(T entidad);

    Optional<T> buscarPorId(String id);

    List<T> listar();

    void eliminar(String id);
}
