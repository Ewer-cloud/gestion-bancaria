src/test/java/cooperativa/repositorio/memoria/RepositorioMemoriaTest.javapackage cooperativa.servicio;
        src/test/java/cooperativa/repositorio/memoria/RepositorioMemoriaTest.javapackage cooperativa.servicio;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.cuenta.CuentaAhorro;
import cooperativa.modelo.cuenta.CuentaCorriente;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.MontoInvalidoException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.memoria.RepositorioMemoria;
import cooperativa.servicio.alerta.Alerta;
import cooperativa.servicio.alerta.DetectorFraude;
import cooperativa.servicio.alerta.NivelRiesgo;
import cooperativa.servicio.alerta.ReglaMontoAlto;
import cooperativa.servicio.busqueda.CriterioMonto;
import cooperativa.servicio.busqueda.CriterioTipo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Flujo completo de Banco con repositorios en memoria. (M2 - Daniel) */
class BancoTest {
    private final Banco banco = Banco.getInstancia();
    private final List<Alerta> alertas = new ArrayList<>();
    private RepositorioMemoria<Cliente> clientes;
    private RepositorioMemoria<Cuenta> cuentas;
    private RepositorioMemoria<Transaccion> transacciones;

    @BeforeEach
    void preparar() {
        banco.reiniciar(); // el Singleton conserva estado entre pruebas: se deja limpio
        clientes = new RepositorioMemoria<>(Cliente::getId);
        cuentas = new RepositorioMemoria<>(Cuenta::getNumero);
        transacciones = new RepositorioMemoria<>(Transaccion::getId);
        DetectorFraude detector = new DetectorFraude();
        detector.agregarRegla(new ReglaMontoAlto(new BigDecimal("5000")));
        detector.registrarListener(alertas::add);
        banco.configurar(clientes, cuentas, transacciones, detector);
    }

    @AfterEach
    void limpiar() {
        banco.reiniciar();
    }

    private static BigDecimal monto(String valor) {
        return new BigDecimal(valor);
    }

    private static void assertSaldo(String esperado, Cuenta cuenta) {
        assertEquals(0, monto(esperado).compareTo(cuenta.getSaldo()),
                "saldo esperado " + esperado + " pero fue " + cuenta.getSaldo());
    }

    private Cuenta cuentaAhorroConSaldo(String saldo) throws Exception {
        Cliente cliente = banco.crearCliente("Ana", "300");
        Cuenta cuenta = banco.abrirCuenta(cliente.getId(), "AHORRO");
        banco.depositar(cuenta.getNumero(), monto(saldo));
        return cuenta;
    }

    // ---------- Clientes y cuentas ----------

    @Test
    void crearClienteGeneraIdsConsecutivos() {
        assertEquals("CLI-001", banco.crearCliente("Ana", "300").getId());
        assertEquals("CLI-002", banco.crearCliente("Luis", "301").getId());
        assertEquals(2, banco.listarClientes().size());
    }

    @Test
    void crearClienteConNombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> banco.crearCliente("  ", "300"));
    }

    @Test
    void abrirCuentaParaClienteInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> banco.abrirCuenta("CLI-999", "AHORRO"));
    }

    @Test
    void abrirCuentaCreaElTipoCorrectoConSaldoCero() {
        Cliente cliente = banco.crearCliente("Ana", "300");
        Cuenta ahorro = banco.abrirCuenta(cliente.getId(), "AHORRO");
        Cuenta corriente = banco.abrirCuenta(cliente.getId(), "CORRIENTE");
        assertInstanceOf(CuentaAhorro.class, ahorro);
        assertInstanceOf(CuentaCorriente.class, corriente);
        assertSaldo("0", ahorro);
        assertEquals(2, banco.listarCuentasDeCliente(cliente.getId()).size());
    }

    @Test
    void listarCuentasDeClienteSoloDevuelveLasSuyas() {
        Cliente ana = banco.crearCliente("Ana", "300");
        Cliente luis = banco.crearCliente("Luis", "301");
        banco.abrirCuenta(ana.getId(), "AHORRO");
        banco.abrirCuenta(luis.getId(), "AHORRO");
        assertEquals(1, banco.listarCuentasDeCliente(ana.getId()).size());
    }

    @Test
    void bloquearYDesbloquearCambianElEstado() {
        Cliente cliente = banco.crearCliente("Ana", "300");
        Cuenta cuenta = banco.abrirCuenta(cliente.getId(), "AHORRO");
        banco.bloquear(cuenta.getNumero());
        assertTrue(cuentas.buscarPorId(cuenta.getNumero()).orElseThrow().isBloqueada());
        banco.desbloquear(cuenta.getNumero());
        assertFalse(cuentas.buscarPorId(cuenta.getNumero()).orElseThrow().isBloqueada());
    }

    // ---------- Operaciones ----------

    @Test
    void depositarSumaAlSaldoYRegistraLaTransaccion() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        assertSaldo("1000", cuenta);
        List<Transaccion> registradas = banco.listarTransacciones();
        assertEquals(1, registradas.size());
        assertEquals(TipoTransaccion.DEPOSITO, registradas.get(0).getTipo());
        assertEquals("TRX-0001", registradas.get(0).getId());
        assertEquals(cuenta.getNumero(), registradas.get(0).getNumeroCuenta());
    }

    @Test
    void retirarRestaDelSaldoYRegistraLaTransaccion() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        banco.retirar(cuenta.getNumero(), monto("400"));
        assertSaldo("600", cuentas.buscarPorId(cuenta.getNumero()).orElseThrow());
        assertEquals(TipoTransaccion.RETIRO, banco.listarTransacciones().get(1).getTipo());
    }

    @Test
    void retiroConSaldoInsuficienteLanzaExcepcionYNoRegistraNada() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        assertThrows(SaldoInsuficienteException.class, () -> banco.retirar(cuenta.getNumero(), monto("1001")));
        assertSaldo("1000", cuentas.buscarPorId(cuenta.getNumero()).orElseThrow());
        assertEquals(1, banco.listarTransacciones().size()); // solo el depósito inicial
    }

    @Test
    void operarSobreCuentaBloqueadaLanzaExcepcion() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        banco.bloquear(cuenta.getNumero());
        assertThrows(CuentaBloqueadaException.class, () -> banco.depositar(cuenta.getNumero(), monto("10")));
        assertThrows(CuentaBloqueadaException.class, () -> banco.retirar(cuenta.getNumero(), monto("10")));
        assertEquals(1, banco.listarTransacciones().size());
    }

    @Test
    void montoInvalidoLanzaExcepcionDelModelo() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        assertThrows(MontoInvalidoException.class, () -> banco.depositar(cuenta.getNumero(), BigDecimal.ZERO));
        assertThrows(MontoInvalidoException.class, () -> banco.retirar(cuenta.getNumero(), monto("-5")));
    }

    @Test
    void operarSobreCuentaInexistenteLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> banco.depositar("CUE-999", monto("10")));
    }

    // ---------- Transferencias ----------

    @Test
    void transferirMueveElDineroYRegistraDosTransacciones() throws Exception {
        Cuenta origen = cuentaAhorroConSaldo("1000");
        Cuenta destino = banco.abrirCuenta(origen.getIdTitular(), "CORRIENTE");

        banco.transferir(origen.getNumero(), destino.getNumero(), monto("300"));

        assertSaldo("700", cuentas.buscarPorId(origen.getNumero()).orElseThrow());
        assertSaldo("300", cuentas.buscarPorId(destino.getNumero()).orElseThrow());
        List<Transaccion> todas = banco.listarTransacciones();
        assertEquals(3, todas.size()); // depósito inicial + salida + entrada
        assertEquals(TipoTransaccion.TRANSFERENCIA_SALIDA, todas.get(1).getTipo());
        assertEquals(origen.getNumero(), todas.get(1).getNumeroCuenta());
        assertEquals(TipoTransaccion.TRANSFERENCIA_ENTRADA, todas.get(2).getTipo());
        assertEquals(destino.getNumero(), todas.get(2).getNumeroCuenta());
    }

    @Test
    void transferirSinSaldoLanzaExcepcionYNadaCambia() throws Exception {
        Cuenta origen = cuentaAhorroConSaldo("100");
        Cuenta destino = banco.abrirCuenta(origen.getIdTitular(), "AHORRO");
        assertThrows(SaldoInsuficienteException.class,
                () -> banco.transferir(origen.getNumero(), destino.getNumero(), monto("500")));
        assertSaldo("100", cuentas.buscarPorId(origen.getNumero()).orElseThrow());
        assertSaldo("0", cuentas.buscarPorId(destino.getNumero()).orElseThrow());
        assertEquals(1, banco.listarTransacciones().size());
    }

    @Test
    void transferirADestinoBloqueadoDevuelveElDineroAlOrigen() throws Exception {
        Cuenta origen = cuentaAhorroConSaldo("1000");
        Cuenta destino = banco.abrirCuenta(origen.getIdTitular(), "AHORRO");
        banco.bloquear(destino.getNumero());

        assertThrows(CuentaBloqueadaException.class,
                () -> banco.transferir(origen.getNumero(), destino.getNumero(), monto("300")));

        assertSaldo("1000", cuentas.buscarPorId(origen.getNumero()).orElseThrow()); // el retiro se deshizo
        assertEquals(1, banco.listarTransacciones().size());
    }

    @Test
    void transferirALaMismaCuentaLanzaExcepcion() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        assertThrows(IllegalArgumentException.class,
                () -> banco.transferir(cuenta.getNumero(), cuenta.getNumero(), monto("10")));
    }

    // ---------- Cierre de mes, búsqueda y alertas ----------

    @Test
    void cerrarMesAplicaInteresSoloALasCuentasDeAhorro() throws Exception {
        Cuenta ahorro = cuentaAhorroConSaldo("1000");
        Cuenta corriente = banco.abrirCuenta(ahorro.getIdTitular(), "CORRIENTE");
        banco.depositar(corriente.getNumero(), monto("1000"));

        banco.cerrarMes();

        assertSaldo("1010", cuentas.buscarPorId(ahorro.getNumero()).orElseThrow());
        assertSaldo("1000", cuentas.buscarPorId(corriente.getNumero()).orElseThrow());
        List<Transaccion> intereses = banco.buscarTransacciones(new CriterioTipo(TipoTransaccion.INTERES));
        assertEquals(1, intereses.size());
        assertEquals(ahorro.getNumero(), intereses.get(0).getNumeroCuenta());
    }

    @Test
    void buscarTransaccionesFiltraConLosCriterios() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        banco.depositar(cuenta.getNumero(), monto("200"));
        banco.retirar(cuenta.getNumero(), monto("50"));

        List<Transaccion> resultado = banco.buscarTransacciones(new CriterioMonto(monto("100"), monto("500")));

        assertEquals(1, resultado.size());
        assertEquals(0, monto("200").compareTo(resultado.get(0).getMonto()));
    }

    @Test
    void unDepositoGrandeAvisaAlListenerDelDetector() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000");
        assertTrue(alertas.isEmpty());

        banco.depositar(cuenta.getNumero(), monto("9000"));

        assertEquals(1, alertas.size());
        assertEquals(NivelRiesgo.ALTO, alertas.get(0).getNivel());
        assertEquals(cuenta.getNumero(), alertas.get(0).getTransaccion().getNumeroCuenta());
    }

    @Test
    void getDetectorDevuelveElDetectorConfigurado() {
        assertNotNull(banco.getDetector());
    }

    // ---------- Configuración ----------

    @Test
    void sinConfigurarLasOperacionesLanzanIllegalState() {
        banco.reiniciar();
        assertThrows(IllegalStateException.class, () -> banco.crearCliente("Ana", "300"));
        assertThrows(IllegalStateException.class, banco::listarClientes);
    }

    @Test
    void configurarConDatosExistentesContinuaLaSecuenciaDeIds() throws Exception {
        Cuenta cuenta = cuentaAhorroConSaldo("1000"); // CLI-001, CUE-001, TRX-0001

        // "Reiniciar el programa": mismos repositorios (como una BD), Banco configurado de nuevo.
        banco.reiniciar();
        banco.configurar(clientes, cuentas, transacciones, new DetectorFraude());

        assertEquals("CLI-002", banco.crearCliente("Luis", "301").getId());
        assertEquals("CUE-002", banco.abrirCuenta("CLI-002", "AHORRO").getNumero());
        banco.depositar(cuenta.getNumero(), monto("10"));
        assertEquals("TRX-0002", banco.listarTransacciones().get(1).getId());
    }
}
