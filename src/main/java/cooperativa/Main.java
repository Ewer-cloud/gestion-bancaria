package cooperativa;

import cooperativa.modelo.cliente.Cliente;
import cooperativa.modelo.cuenta.Cuenta;
import cooperativa.modelo.transaccion.Transaccion;
import cooperativa.repositorio.Repositorio;
import cooperativa.repositorio.memoria.RepositorioMemoria;
import cooperativa.repositorio.sqlite.ConexionDB;
import cooperativa.repositorio.sqlite.RepositorioClienteSQLite;
import cooperativa.repositorio.sqlite.RepositorioCuentaSQLite;
import cooperativa.repositorio.sqlite.RepositorioTransaccionSQLite;
import cooperativa.servicio.Banco;
import cooperativa.servicio.alerta.DetectorFraude;
import cooperativa.servicio.alerta.ReglaMontoAlto;
import cooperativa.servicio.alerta.ReglaRafaga;
import cooperativa.vista.Tema;
import cooperativa.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.time.Duration;

/**
 * Punto de entrada: arma las piezas, configura Banco y abre la ventana. (Integración - Ewer)
 * <p>
 * Por defecto guarda en SQLite (archivo cooperativa.db). Con el argumento {@code --memoria}
 * usa repositorios en memoria (los datos se pierden al cerrar).
 */
public class Main {
    /** Monto desde el cual se considera sospechoso un movimiento. */
    private static final BigDecimal UMBRAL_MONTO_ALTO = new BigDecimal("5000000");
    /** Ráfaga: esta cantidad de movimientos de una misma cuenta... */
    private static final int MOVIMIENTOS_RAFAGA = 3;
    /** ...dentro de esta ventana de tiempo. */
    private static final Duration VENTANA_RAFAGA = Duration.ofMinutes(1);

    public static void main(String[] args) {
        boolean enMemoria = args.length > 0 && args[0].equals("--memoria");

        // 1. Repositorios (memoria o SQLite)
        Repositorio<Cliente> clientes;
        Repositorio<Cuenta> cuentas;
        Repositorio<Transaccion> transacciones;
        if (enMemoria) {
            clientes = new RepositorioMemoria<>(Cliente::getId);
            cuentas = new RepositorioMemoria<>(Cuenta::getNumero);
            transacciones = new RepositorioMemoria<>(Transaccion::getId);
        } else {
            ConexionDB.inicializar();
            clientes = new RepositorioClienteSQLite();
            cuentas = new RepositorioCuentaSQLite();
            transacciones = new RepositorioTransaccionSQLite();
        }

        // 2. Detector de fraude con sus reglas
        DetectorFraude detector = new DetectorFraude();
        detector.agregarRegla(new ReglaMontoAlto(UMBRAL_MONTO_ALTO));
        detector.agregarRegla(new ReglaRafaga(MOVIMIENTOS_RAFAGA, VENTANA_RAFAGA));

        // 3. Banco recibe todo desde afuera
        Banco banco = Banco.getInstancia();
        banco.configurar(clientes, cuentas, transacciones, detector);

        // 4. Datos de ejemplo solo si no hay nada (para no duplicarlos en cada arranque)
        if (banco.listarClientes().isEmpty()) {
            DatosDemo.cargar(banco);
        }

        // 5. Ventana (siempre en el hilo de Swing)
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            new VentanaPrincipal(banco).setVisible(true);
        });
    }
}
