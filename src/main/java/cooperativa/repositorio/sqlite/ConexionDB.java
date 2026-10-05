package cooperativa.repositorio.sqlite;

import java.sql.Connection;

/** Conexión a SQLite. Todo el JDBC del proyecto vive solo en este package. (M2b - Daniel) */
public final class ConexionDB {
    public static final String URL = "jdbc:sqlite:cooperativa.db";

    private ConexionDB() {
    }

    /** Abre una conexión con PRAGMA foreign_keys = ON. Convierte SQLException en RepositorioException. */
    public static Connection obtener() {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    /** Crea las tablas si no existen. */
    public static void inicializar() {
        // TODO M2b
    }
}
