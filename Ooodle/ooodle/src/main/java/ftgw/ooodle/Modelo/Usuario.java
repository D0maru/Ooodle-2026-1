package ftgw.ooodle.Modelo;
/**
 * Representa a un usuario registrado en la aplicación.
 * Almacena su identificador, nombre de usuario y permisos
 * relacionados al modo de juego diario.
 */
public class Usuario {
    /** Identificador único del usuario en el sistema. */
    public int id;
    /** Nombre de usuario mostrado en la aplicación. */
    private String nickname;
    /** Indica si el usuario está en modo de juego diario. */
    private boolean juegoDiario;
    /** Indica si el usuario tiene permiso para iniciar una partida. */
    private boolean puedeJugar;
    /**
     * Verifica si el usuario está en modo de juego diario.
     * @return true si el usuario juega en modo diario; false en caso contrario.
     */
    public boolean isJuegoDiario() {
        return juegoDiario;
    }
    /**
     * Establece el modo de juego diario del usuario.
     * @param juegoDiario true para activar el modo diario; false para desactivarlo.
     * @return true si la asignación fue exitosa.
     */
    public boolean setJuegoDiario(boolean juegoDiario) {
        this.juegoDiario = juegoDiario;
        return this.juegoDiario == juegoDiario;
    }
    /**
     * Crea un usuario con su identificador, nombre y modo de juego diario.
     * @param id Identificador único del usuario.
     * @param nickname Nombre de usuario.
     * @param juegoDiario true si el usuario jugará en modo diario.
     */
    public Usuario(int id, String nickname, Boolean juegoDiario) {
        this.id = id;
        this.nickname = nickname;
        this.juegoDiario = juegoDiario;
    }
    /**
     * Obtiene el nombre de usuario.
     * @return Cadena con el nickname del usuario.
     */
    public String getNickname() {
        return nickname;
    }
    /**
     * Obtiene el identificador único del usuario.
     * @return Entero con el id del usuario.
     */
    public int getId(){
        return id;
    }
    /**
     * Establece el permiso de juego del usuario.
     * @param puedeJugar true para permitir jugar; false para restringirlo.
     * @return true si el permiso fue asignado correctamente.
     */
    public boolean setPuedeJugar(boolean puedeJugar) {
        this.puedeJugar = puedeJugar;
        return this.puedeJugar;
    }
    /**
     * Verifica si el usuario tiene permiso para jugar en este momento.
     * @return true si el usuario puede jugar; false en caso contrario.
     */
    public boolean isPuedeJugar() {
        return puedeJugar;
    }
}
