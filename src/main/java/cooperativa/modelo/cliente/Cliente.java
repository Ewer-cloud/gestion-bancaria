package cooperativa.modelo.cliente;

/** Cliente de la cooperativa. (M2 - Daniel) */
public class Cliente {
    private final String id;
    private final String nombre;
    private final String telefono;

    public Cliente(String id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    // TODO M2: validar nombre no vacío, equals y hashCode por id, toString
}
