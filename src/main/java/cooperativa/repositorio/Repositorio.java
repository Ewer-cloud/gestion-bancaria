package cooperativa.repositorio;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de almacenamiento. Lo implementan la versión en memoria
 * y las versiones SQLite.
 *
 * @param <T> tipo de entidad que se guarda
 */
public interface Repositorio<T> {
    /** Inserta la entidad o la actualiza si ya existe su id. */
    void guardar(T entidad);

    Optional<T> buscarPorId(String id);

    List<T> listar();

    void eliminar(String id);
}