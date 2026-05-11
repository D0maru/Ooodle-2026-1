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

    public Usuario getUsuarioActual(){
        return usuarioActual;
    }
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual=usuario;
    }
}
