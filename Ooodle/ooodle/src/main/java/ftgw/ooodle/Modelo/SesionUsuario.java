package ftgw.ooodle.Modelo;

public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioActual; // Aquí se guarda el elegido


    public static SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    // El "Setter" para cuando eligen perfil
    public Usuario getUsuarioActual(){
        return usuarioActual;
    }
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual=usuario;
    }
}
