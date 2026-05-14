package ftgw.ooodle.Modelo;
/**
 * Singleton que representa la sesión activa del usuario en la aplicación.
 * Garantiza que solo exista una sesión a la vez y provee acceso global
 * al usuario autenticado actualmente.
 */
public class SesionUsuario {
    /** Única instancia activa de la sesión. Null si no hay sesión iniciada. */
    private static SesionUsuario instancia;
    /** Usuario autenticado asociado a la sesión activa. */
    private Usuario usuarioActual;

    /**
     * Constructor privado. Crea la sesión vinculada al usuario indicado.
     * @param usuario Usuario que iniciará la sesión.
     */
    private SesionUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    /**
     * Crea o reemplaza la sesión activa con el usuario indicado.
     * Debe llamarse antes de getInstancia().
     * @param usuario Usuario con el que se iniciará la sesión; no puede ser nulo.
     * @return La instancia de SesionUsuario creada.
     * @throws IllegalArgumentException si el usuario es nulo.
     */
    public static SesionUsuario iniciarSesion(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("El usuario no puede ser nulo.");
        instancia = new SesionUsuario(usuario);
        return instancia;
    }

    /**
     * Devuelve la sesión activa.
     * @return La instancia actual de SesionUsuario.
     * @throws IllegalStateException si no se ha iniciado sesión previamente.
     */
    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            throw new IllegalStateException("No hay sesión activa. Llama a iniciarSesion(usuario) primero.");
        }
        return instancia;
    }
    /**
     * Cierra y elimina la sesión activa.
     * @return true si había una sesión activa y fue eliminada; false si no había sesión.
     */
    public static boolean resetInstancia() {
        if (instancia == null) return false;
        instancia = null;
        return true;
    }

    /**
     * Verifica si existe una sesión activa en este momento.
     * @return true si hay una sesión iniciada; false en caso contrario.
     */
    public static boolean haySesionActiva() {
        return instancia != null;
    }
    /**
     * Obtiene el usuario asociado a la sesión activa.
     * @return El objeto Usuario de la sesión actual.
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    /**
     * Reemplaza el usuario de la sesión activa.
     * @param usuario Nuevo usuario a asociar a la sesión.
     * @return La misma instancia para permitir encadenamiento de llamadas.
     */
    public SesionUsuario setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        return this;
    }
}