package cooperativa.servicio;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.Repositorio;
import cooperativa.servicio.alerta.DetectorFraude;
import cooperativa.servicio.busqueda.CriterioBusqueda;
import cooperativa.servicio.busqueda.MotorBusqueda;

import java.math.BigDecimal;
import java.util.List;

/**
 * Singleton y fachada: la vista solo habla con Banco. Recibe los repositorios desde afuera,
 * así funciona igual con memoria o con SQLite. (M2 - Daniel)
 */
public final class Banco {
    private static Banco instancia;

    private final Repositorio<Cuenta> cuentas;
    private final Repositorio<Cliente> clientes;
    private final Repositorio<Transaccion> transacciones;
    private final DetectorFraude detector;
    private final MotorBusqueda motor;

    private Banco(Repositorio<Cuenta> cuentas, Repositorio<Cliente> clientes,
                  Repositorio<Transaccion> transacciones, DetectorFraude detector, MotorBusqueda motor) {
        this.cuentas = cuentas;
        this.clientes = clientes;
        this.transacciones = transacciones;
        this.detector = detector;
        this.motor = motor;
    }

    /** Se llama una sola vez al arrancar (desde Main). */
    public static void configurar(Repositorio<Cuenta> cuentas, Repositorio<Cliente> clientes,
                                  Repositorio<Transaccion> transacciones, DetectorFraude detector,
                                  MotorBusqueda motor) {
        instancia = new Banco(cuentas, clientes, transacciones, detector, motor);
    }

    public static Banco getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("Banco no está configurado: llamar a Banco.configurar(...)");
        }
        return instancia;
    }

    /** Solo para pruebas: deja el Singleton limpio. */
    public static void reiniciar() {
        instancia = null;
    }

    public DetectorFraude getDetector() {
        return detector;
    }

    // ---- Clientes y cuentas ----

    public Cliente crearCliente(String nombre, String telefono) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    /** @param tipo "AHORRO" o "CORRIENTE" */
    public Cuenta abrirCuenta(String idCliente, String tipo, BigDecimal saldoInicial) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public void bloquearCuenta(String numeroCuenta) {
        // TODO M2
    }

    public void desbloquearCuenta(String numeroCuenta) {
        // TODO M2
    }

    // ---- Operaciones ----

    public void depositar(String numeroCuenta, BigDecimal monto) throws CuentaBloqueadaException {
        // TODO M2: validar, modificar cuenta, guardar cuenta, guardar transacción, notificar al detector
    }

    public void retirar(String numeroCuenta, BigDecimal monto)
            throws SaldoInsuficienteException, CuentaBloqueadaException {
        // TODO M2
    }

    /** Coordina dos cuentas: retira del origen, deposita en el destino y registra dos transacciones. */
    public void transferir(String numeroOrigen, String numeroDestino, BigDecimal monto)
            throws SaldoInsuficienteException, CuentaBloqueadaException {
        // TODO M2
    }

    /** Aplica intereses a las cuentas que los generan (meta). */
    public void cerrarMes() {
        // TODO M2 meta
    }

    // ---- Consultas ----

    public List<Cliente> listarClientes() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public List<Cuenta> listarCuentas() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public List<Cuenta> listarCuentasDeCliente(String idCliente) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public List<Transaccion> listarTransacciones() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    public List<Transaccion> buscarTransacciones(CriterioBusqueda... criterios) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
