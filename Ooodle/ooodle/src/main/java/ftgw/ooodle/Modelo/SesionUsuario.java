package ftgw.ooodle.Modelo;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioActual;

    // Constructor privado: requiere un Usuario para crear la sesión
    private SesionUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    /**
     * Crea (o reemplaza) la sesión con el usuario indicado.
     * Debe llamarse antes de getInstancia().
     */
    public static SesionUsuario iniciarSesion(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo.");
        instancia = new SesionUsuario(usuario);
        return instancia;
    }

    /**
     * Devuelve la sesión activa. Lanza IllegalStateException si no se inició sesión.
     */
    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("No hay sesión activa. Llama a iniciarSesion(usuario) primero.");
        }
        return instancia;
    }

    public static boolean resetInstancia() {
        if (instancia == null) return false;
        instancia = null;
        return true;
    }

    public static boolean haySesionActiva() {
        return instancia != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /** Retorna this para encadenamiento */
    public SesionUsuario setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        return this;
    }
}