package cooperativa.repositorio.sqlite;

import cooperativa.modelo.excepcion.RepositorioException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/** Conexión a SQLite. Todo el JDBC del proyecto vive solo en este package. (M2b - Daniel) */
public final class ConexionDB {
    public static final String URL = "jdbc:sqlite:cooperativa.db";

    /** URL en uso. Las pruebas la cambian a una base temporal con {@link #usarUrl}. */
    private static String urlActual = URL;

    private ConexionDB() {
    }

    /** Cambia la base de datos a usar (útil para pruebas con un archivo temporal). */
    public static synchronized void usarUrl(String url) {
        urlActual = Objects.requireNonNull(url, "url");
    }

    /** Vuelve a la base de datos real (cooperativa.db). */
    public static synchronized void restaurarUrl() {
        urlActual = URL;
    }

    /** Abre una conexión con PRAGMA foreign_keys = ON. Convierte SQLException en RepositorioException. */
    public static Connection obtener() {
        String url;
        synchronized (ConexionDB.class) {
            url = urlActual;
        }
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(url);
            try (Statement st = conexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            return conexion;
        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException ignorada) {
                    // ya se va a lanzar el error original
                }
            }
            throw new RepositorioException("No se pudo abrir la base de datos", e);
        }
    }

    /** Crea las tablas si no existen. El orden importa por las llaves foráneas. */
    public static void inicializar() {
        try (Connection conexion = obtener(); Statement st = conexion.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS cliente (
                        id       TEXT PRIMARY KEY,
                        nombre   TEXT NOT NULL,
                        telefono TEXT NOT NULL DEFAULT ''
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS cuenta (
                        numero     TEXT PRIMARY KEY,
                        tipo       TEXT NOT NULL CHECK (tipo IN ('AHORRO', 'CORRIENTE')),
                        saldo      TEXT NOT NULL,
                        id_titular TEXT NOT NULL REFERENCES cliente(id),
                        bloqueada  INTEGER NOT NULL DEFAULT 0 CHECK (bloqueada IN (0, 1))
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS transaccion (
                        id            TEXT PRIMARY KEY,
                        numero_cuenta TEXT NOT NULL REFERENCES cuenta(numero),
                        tipo          TEXT NOT NULL,
                        monto         TEXT NOT NULL,
                        fecha         TEXT NOT NULL
                    )""");
        } catch (SQLException e) {
            throw new RepositorioException("No se pudieron crear las tablas", e);
        }
    }
}
