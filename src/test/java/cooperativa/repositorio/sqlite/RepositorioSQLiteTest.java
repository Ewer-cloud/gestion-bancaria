package cooperativa.repositorio.sqlite;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.cuenta.CuentaAhorro;
import cooperativa.modelo.cuenta.CuentaCorriente;
import cooperativa.modelo.excepcion.RepositorioException;
import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.DetectorFraude;
import cooperativa.servicio.alerta.ReglaMontoAlto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de los repositorios SQLite sobre una base de datos temporal (no el archivo real). (M2b) */
class RepositorioSQLiteTest {
    private final RepositorioClienteSQLite clientes = new RepositorioClienteSQLite();
    private final RepositorioCuentaSQLite cuentas = new RepositorioCuentaSQLite();
    private final RepositorioTransaccionSQLite transacciones = new RepositorioTransaccionSQLite();

    @BeforeEach
    void prepararBaseTemporal(@TempDir Path carpeta) {
        ConexionDB.usarUrl("jdbc:sqlite:" + carpeta.resolve("prueba.db"));
        ConexionDB.inicializar();
    }

    @AfterEach
    void restaurar() {
        ConexionDB.restaurarUrl();
        Banco.getInstancia().reiniciar();
    }

    @Test
    void guardarYBuscarCliente() {
        clientes.guardar(new Cliente("CLI-001", "Ana Pérez", "300"));
        Cliente leido = clientes.buscarPorId("CLI-001").orElseThrow();
        assertEquals("Ana Pérez", leido.getNombre());
        assertEquals("300", leido.getTelefono());
        assertTrue(clientes.buscarPorId("CLI-999").isEmpty());
    }

    @Test
    void guardarDosVecesActualizaEnLugarDeDuplicar() {
        clientes.guardar(new Cliente("CLI-001", "Ana", "300"));
        clientes.guardar(new Cliente("CLI-001", "Ana María", "301"));
        assertEquals(1, clientes.listar().size());
        assertEquals("Ana María", clientes.buscarPorId("CLI-001").orElseThrow().getNombre());
    }

    @Test
    void cuentaReconstruyeElTipoCorrectoConLaFactory() {
        clientes.guardar(new Cliente("CLI-001", "Ana", "300"));
        cuentas.guardar(new CuentaAhorro("CUE-001", "CLI-001", new BigDecimal("1000.50"), false));
        cuentas.guardar(new CuentaCorriente("CUE-002", "CLI-001", new BigDecimal("-200"), true));

        Cuenta ahorro = cuentas.buscarPorId("CUE-001").orElseThrow();
        Cuenta corriente = cuentas.buscarPorId("CUE-002").orElseThrow();

        assertInstanceOf(CuentaAhorro.class, ahorro);
        assertInstanceOf(CuentaCorriente.class, corriente);
        assertEquals(0, new BigDecimal("1000.50").compareTo(ahorro.getSaldo()));
        assertEquals(0, new BigDecimal("-200").compareTo(corriente.getSaldo()));
        assertFalse(ahorro.isBloqueada());
        assertTrue(corriente.isBloqueada());
        assertEquals(2, cuentas.listar().size());
    }

    @Test
    void actualizarCuentaNoRompeLasTransaccionesQueLaReferencian() {
        clientes.guardar(new Cliente("CLI-001", "Ana", "300"));
        cuentas.guardar(new CuentaAhorro("CUE-001", "CLI-001", BigDecimal.TEN, false));
        transacciones.guardar(new Transaccion("TRX-0001", "CUE-001", TipoTransaccion.DEPOSITO,
                BigDecimal.TEN, LocalDateTime.now()));

        cuentas.guardar(new CuentaAhorro("CUE-001", "CLI-001", new BigDecimal("99"), false));

        assertEquals(0, new BigDecimal("99").compareTo(cuentas.buscarPorId("CUE-001").orElseThrow().getSaldo()));
        assertEquals(1, transacciones.listar().size());
    }

    @Test
    void transaccionSeGuardaYSeLeeIgual() {
        clientes.guardar(new Cliente("CLI-001", "Ana", "300"));
        cuentas.guardar(new CuentaAhorro("CUE-001", "CLI-001", BigDecimal.TEN, false));
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 18, 14, 30, 5, 123_000_000);
        transacciones.guardar(new Transaccion("TRX-0001", "CUE-001", TipoTransaccion.RETIRO,
                new BigDecimal("12.34"), fecha));

        Transaccion leida = transacciones.buscarPorId("TRX-0001").orElseThrow();
        assertEquals(TipoTransaccion.RETIRO, leida.getTipo());
        assertEquals(0, new BigDecimal("12.34").compareTo(leida.getMonto()));
        assertEquals(fecha, leida.getFecha());
        assertEquals("CUE-001", leida.getNumeroCuenta());
    }

    @Test
    void llaveForaneaImpideCuentaDeClienteInexistente() {
        Cuenta huerfana = new CuentaAhorro("CUE-001", "CLI-NO-EXISTE", BigDecimal.ZERO, false);
        assertThrows(RepositorioException.class, () -> cuentas.guardar(huerfana));
    }

    @Test
    void llaveForaneaImpideTransaccionDeCuentaInexistente() {
        Transaccion huerfana = new Transaccion("TRX-0001", "CUE-NO-EXISTE", TipoTransaccion.DEPOSITO,
                BigDecimal.TEN, LocalDateTime.now());
        assertThrows(RepositorioException.class, () -> transacciones.guardar(huerfana));
    }

    @Test
    void eliminarQuitaLaFila() {
        clientes.guardar(new Cliente("CLI-001", "Ana", "300"));
        clientes.eliminar("CLI-001");
        assertTrue(clientes.listar().isEmpty());
    }

    @Test
    void listarConservaElOrdenDeInsercion() {
        clientes.guardar(new Cliente("CLI-003", "C", ""));
        clientes.guardar(new Cliente("CLI-001", "A", ""));
        clientes.guardar(new Cliente("CLI-002", "B", ""));
        List<String> ids = clientes.listar().stream().map(Cliente::getId).toList();
        assertEquals(List.of("CLI-003", "CLI-001", "CLI-002"), ids);
    }

    /** El requisito de la demo: cerrar el programa, abrirlo de nuevo y que los datos sigan ahí. */
    @Test
    void losDatosSobrevivenAlReinicioDelBanco() throws Exception {
        Banco banco = Banco.getInstancia();
        List<Alerta> alertas = new ArrayList<>();
        DetectorFraude detector = new DetectorFraude();
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("1000")));
        detector.registrarListener(alertas::add);

        banco.reiniciar();
        banco.configurar(new RepositorioClienteSQLite(), new RepositorioCuentaSQLite(),
                new RepositorioTransaccionSQLite(), detector);
        Cliente ana = banco.crearCliente("Ana", "300");
        Cuenta cuenta = banco.abrirCuenta(ana.getId(), "AHORRO");
        banco.depositar(cuenta.getNumero(), new BigDecimal("5000")); // supera el umbral: alerta
        assertEquals(1, alertas.size());

        // "Cerrar el programa": se descarta todo el estado en memoria
        banco.reiniciar();

        // "Abrir de nuevo": nuevos repositorios sobre el mismo archivo .db
        banco.configurar(new RepositorioClienteSQLite(), new RepositorioCuentaSQLite(),
                new RepositorioTransaccionSQLite(), new DetectorFraude());

        assertEquals(1, banco.listarClientes().size());
        assertEquals(1, banco.listarCuentas().size());
        assertEquals(0, new BigDecimal("5000").compareTo(banco.listarCuentas().get(0).getSaldo()));
        assertEquals(1, banco.listarTransacciones().size());

        // Los ids nuevos continúan la secuencia en vez de reiniciarse
        assertEquals("CLI-002", banco.crearCliente("Luis", "310").getId());
        // Y la búsqueda funciona sobre los datos persistidos
        assertEquals(1, banco.buscarTransacciones().size());
    }

    @Test
    void transferenciaPersisteAmbasCuentasYDosTransacciones() throws Exception {
        Banco banco = Banco.getInstancia();
        banco.reiniciar();
        banco.configurar(clientes, cuentas, transacciones, new DetectorFraude());
        Cliente ana = banco.crearCliente("Ana", "300");
        Cuenta origen = banco.abrirCuenta(ana.getId(), "AHORRO");
        Cuenta destino = banco.abrirCuenta(ana.getId(), "CORRIENTE");
        banco.depositar(origen.getNumero(), new BigDecimal("1000"));
        banco.transferir(origen.getNumero(), destino.getNumero(), new BigDecimal("400"));

        assertEquals(0, new BigDecimal("600").compareTo(cuentas.buscarPorId(origen.getNumero()).orElseThrow().getSaldo()));
        assertEquals(0, new BigDecimal("400").compareTo(cuentas.buscarPorId(destino.getNumero()).orElseThrow().getSaldo()));
        assertEquals(3, transacciones.listar().size()); // depósito + salida + entrada
    }
}
