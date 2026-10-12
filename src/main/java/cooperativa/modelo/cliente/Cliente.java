package cooperativa.modelo.cliente;

import java.util.Objects;

/** Asociado de la cooperativa. Identificado por su id (ej. CLI-001). */
public class Cliente {
    private final String id;
    private String nombre;
    private String telefono;

    public Cliente(String id, String nombre, String telefono) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del cliente no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return id + " - " + nombre + " (" + telefono + ")"; }
}