package cooperativa.servicio;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.cuenta.CuentaFactory;
import cooperativa.modelo.cuenta.GeneradoraDeIntereses;
import cooperativa.modelo.excepcion.CuentaBloqueadaException;
import cooperativa.modelo.excepcion.SaldoInsuficienteException;
import cooperativa.modelo.transaccion.TipoTransaccion;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.Repositorio;
import cooperativa.servicio.alerta.DetectorFraude;
import cooperativa.servicio.busqueda.CriterioBusqueda;
import cooperativa.servicio.busqueda.MotorBusqueda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Fachada y Singleton del sistema. La vista solo llama a este servicio.
 * No sabe si los datos viven en memoria o en SQLite: recibe los repositorios
 * desde afuera con {@link #configurar}.
 * Orden de cada operación: validar, modificar cuenta, guardar cuenta,
 * guardar transacción y notificar al detector.
 */
public class Banco {
    private static Banco instancia;

    private Repositorio<Cliente> clientes;
    private Repositorio<Cuenta> cuentas;
    private Repositorio<Transaccion> transacciones;
    private DetectorFraude detector;

    private int contadorClientes;
    private int contadorCuentas;
    private int contadorTransacciones;

    private Banco() { }

    public static synchronized Banco getInstancia() {
        if (instancia == null) {
            instancia = new Banco();
        }
        return instancia;
    }

    /** Se llama una vez al arrancar (Main) o al inicio de cada prueba. */
    public synchronized void configurar(Repositorio<Cliente> clientes,
                                        Repositorio<Cuenta> cuentas,
                                        Repositorio<Transaccion> transacciones,
                                        DetectorFraude detector) {
        this.clientes = clientes;
        this.cuentas = cuentas;
        this.transacciones = transacciones;
        this.detector = detector;
        // Con SQLite los datos ya existen: los ids nuevos deben continuar la secuencia.
        this.contadorClientes = mayorSecuencia(clientes.listar().stream().map(Cliente::getId).toList(), "CLI");
        this.contadorCuentas = mayorSecuencia(cuentas.listar().stream().map(Cuenta::getNumero).toList(), "CUE");
        this.contadorTransacciones = mayorSecuencia(transacciones.listar().stream().map(Transaccion::getId).toList(), "TRX");
    }

    /** Deja el Singleton limpio para que las pruebas empiecen de cero. */
    public synchronized void reiniciar() {
        clientes = null;
        cuentas = null;
        transacciones = null;
        detector = null;
        contadorClientes = 0;
        contadorCuentas = 0;
        contadorTransacciones = 0;
    }

    // ---------- Clientes y cuentas ----------

    public Cliente crearCliente(String nombre, String telefono) {
        verificarConfigurado();
        Cliente cliente = new Cliente(String.format("CLI-%03d", ++contadorClientes), nombre, telefono);
        clientes.guardar(cliente);
        return cliente;
    }

    /** @param tipo "AHORRO" o "CORRIENTE" */
    public Cuenta abrirCuenta(String idCliente, String tipo) {
        verificarConfigurado();
        clientes.buscarPorId(idCliente)
                .orElseThrow(() -> new IllegalArgumentException("No existe el cliente " + idCliente));
        String numero = String.format("CUE-%03d", ++contadorCuentas);
        Cuenta cuenta = CuentaFactory.crear(tipo, numero, idCliente, BigDecimal.ZERO, false);
        cuentas.guardar(cuenta);
        return cuenta;
    }

    public void bloquear(String numeroCuenta) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        cuenta.bloquear();
        cuentas.guardar(cuenta);
    }

    public void desbloquear(String numeroCuenta) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        cuenta.desbloquear();
        cuentas.guardar(cuenta);
    }

    // ---------- Operaciones ----------

    public void depositar(String numeroCuenta, BigDecimal monto) throws CuentaBloqueadaException {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        cuenta.depositar(monto);
        cuentas.guardar(cuenta);
        registrar(numeroCuenta, TipoTransaccion.DEPOSITO, monto);
    }

    public void retirar(String numeroCuenta, BigDecimal monto)
            throws CuentaBloqueadaException, SaldoInsuficienteException {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        cuenta.retirar(monto);
        cuentas.guardar(cuenta);
        registrar(numeroCuenta, TipoTransaccion.RETIRO, monto);
    }

    /** Retira del origen, deposita en el destino y registra dos transacciones. */
    public void transferir(String numeroOrigen, String numeroDestino, BigDecimal monto)
            throws CuentaBloqueadaException, SaldoInsuficienteException {
        if (numeroOrigen.equals(numeroDestino)) {
            throw new IllegalArgumentException("La cuenta origen y destino deben ser distintas");
        }
        Cuenta origen = obtenerCuenta(numeroOrigen);
        Cuenta destino = obtenerCuenta(numeroDestino);

        origen.retirar(monto);
        try {
            destino.depositar(monto);
        } catch (CuentaBloqueadaException | RuntimeException e) {
            origen.depositar(monto); // deshacer el retiro para no perder dinero
            throw e;
        }
        cuentas.guardar(origen);
        cuentas.guardar(destino);
        registrar(numeroOrigen, TipoTransaccion.TRANSFERENCIA_SALIDA, monto);
        registrar(numeroDestino, TipoTransaccion.TRANSFERENCIA_ENTRADA, monto);
    }

    /** Capa 2: aplica interés a las cuentas de ahorro y registra cada movimiento. */
    public void cerrarMes() {
        verificarConfigurado();
        for (Cuenta cuenta : cuentas.listar()) {
            if (cuenta instanceof GeneradoraDeIntereses generadora) {
                BigDecimal interes = generadora.calcularInteres();
                if (interes.signum() > 0) {
                    generadora.aplicarInteres();
                    cuentas.guardar(cuenta);
                    registrar(cuenta.getNumero(), TipoTransaccion.INTERES, interes);
                }
            }
        }
    }

    // ---------- Consultas ----------

    /** Detector de fraude configurado; la vista lo usa para registrar su AlertaListener. */
    public DetectorFraude getDetector() {
        verificarConfigurado();
        return detector;
    }

    public List<Cliente> listarClientes() {
        verificarConfigurado();
        return clientes.listar();
    }

    public List<Cuenta> listarCuentas() {
        verificarConfigurado();
        return cuentas.listar();
    }

    public List<Cuenta> listarCuentasDeCliente(String idCliente) {
        verificarConfigurado();
        return cuentas.listar().stream()
                .filter(c -> c.getIdTitular().equals(idCliente))
                .toList();
    }

    public List<Transaccion> listarTransacciones() {
        verificarConfigurado();
        return transacciones.listar();
    }

    /** Entrega la lista de transacciones al motor de búsqueda de Ewer. */
    public List<Transaccion> buscarTransacciones(CriterioBusqueda... criterios) {
        verificarConfigurado();
        return new MotorBusqueda().buscar(transacciones.listar(), criterios);
    }

    // ---------- Internos ----------

    private Cuenta obtenerCuenta(String numero) {
        verificarConfigurado();
        return cuentas.buscarPorId(numero)
                .orElseThrow(() -> new IllegalArgumentException("No existe la cuenta " + numero));
    }

    private void registrar(String numeroCuenta, TipoTransaccion tipo, BigDecimal monto) {
        Transaccion t = new Transaccion(String.format("TRX-%04d", ++contadorTransacciones),
                numeroCuenta, tipo, monto, LocalDateTime.now());
        transacciones.guardar(t);
        if (detector != null) {
            detector.analizar(t, transacciones.listar());
        }
    }

    private void verificarConfigurado() {
        if (clientes == null) {
            throw new IllegalStateException("Banco no está configurado: llama a configurar() primero");
        }
    }

    private static int mayorSecuencia(List<String> ids, String prefijo) {
        int mayor = 0;
        for (String id : ids) {
            if (id.startsWith(prefijo + "-")) {
                try {
                    mayor = Math.max(mayor, Integer.parseInt(id.substring(prefijo.length() + 1)));
                } catch (NumberFormatException ignorada) {
                    // id con otro formato: se ignora
                }
            }
        }
        return mayor;
    }
}