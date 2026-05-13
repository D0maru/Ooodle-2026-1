package ftgw.ooodle.Modelo;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioActual;

    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public static boolean resetInstancia() {
        if (instancia == null) return false;
        instancia = null;
        return true;
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