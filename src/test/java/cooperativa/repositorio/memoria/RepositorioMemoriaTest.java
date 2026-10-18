package cooperativa.repositorio.memoria;

import cooperativa.modelo.cliente.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Un solo Repositorio genérico sirve para cualquier entidad gracias al extractor de id. (M2 - Daniel) */
class RepositorioMemoriaTest {
    private final RepositorioMemoria<Cliente> repositorio = new RepositorioMemoria<>(Cliente::getId);

    @Test
    void guardarYBuscarPorId() {
        Cliente ana = new Cliente("CLI-001", "Ana", "300");
        repositorio.guardar(ana);
        assertEquals(ana, repositorio.buscarPorId("CLI-001").orElseThrow());
    }

    @Test
    void buscarUnIdInexistenteDevuelveVacio() {
        assertTrue(repositorio.buscarPorId("CLI-999").isEmpty());
    }

    @Test
    void guardarConElMismoIdReemplazaSinDuplicar() {
        repositorio.guardar(new Cliente("CLI-001", "Ana", "300"));
        repositorio.guardar(new Cliente("CLI-001", "Ana María", "300"));
        assertEquals(1, repositorio.listar().size());
        assertEquals("Ana María", repositorio.buscarPorId("CLI-001").orElseThrow().getNombre());
    }

    @Test
    void listarConservaElOrdenDeInsercion() {
        repositorio.guardar(new Cliente("CLI-002", "Luis", "301"));
        repositorio.guardar(new Cliente("CLI-001", "Ana", "300"));
        assertEquals("CLI-002", repositorio.listar().get(0).getId());
    }

    @Test
    void eliminarQuitaElElemento() {
        repositorio.guardar(new Cliente("CLI-001", "Ana", "300"));
        repositorio.eliminar("CLI-001");
        assertTrue(repositorio.listar().isEmpty());
    }

    @Test
    void modificarLaListaDevueltaNoAfectaAlRepositorio() {
        repositorio.guardar(new Cliente("CLI-001", "Ana", "300"));
        repositorio.listar().clear();
        assertEquals(1, repositorio.listar().size());
    }
}
