package ftgw.ooodle.Modelo;
/**
 * Representa el resultado de una partida finalizada.
 * Almacena los cambios generados en las estadísticas del usuario
 * y los valores acumulados tras aplicar dichos cambios.
 */
public class ResultadoPartida {
    /** Identificador del usuario al que pertenece este resultado. */
    public int idUsuario;
    /** Variación aplicada a la racha del usuario (+1 si ganó, -racha si perdió). */
    public int cambioRacha;   
    /** Variación aplicada al contador de partidas ganadas (1 si ganó, 0 si perdió). */
    public int cambioGanadas; 
    /** Variación aplicada al contador de partidas jugadas (siempre 1). */
    public int cambioJugadas; 
    /** Valor de la racha del usuario tras aplicar el cambio. */
    public int rachaActual; 
    /** Racha máxima histórica del usuario tras la partida. */
    public int rachaMax;
    /** Total de partidas jugadas por el usuario tras la partida. */
    public int partidasJugadas;
    /** Total de partidas ganadas por el usuario tras la partida. */
    public int partidasGanadas;
    /**
     * Crea un resultado de partida con los cambios estadísticos generados.
     * @param idUsuario Identificador del usuario.
     * @param cambioRacha Cambio a aplicar en la racha.
     * @param cambioGanadas Cambio a aplicar en partidas ganadas.
     * @param cambioJugadas Cambio a aplicar en partidas jugadas.
     */
    public ResultadoPartida(int idUsuario, int cambioRacha, int cambioGanadas, int cambioJugadas) {
        this.idUsuario = idUsuario;
        this.cambioRacha = cambioRacha;
        this.cambioGanadas = cambioGanadas;
        this.cambioJugadas = cambioJugadas;
    }
    /**
     * Obtiene el identificador del usuario.
     * @return Entero con el id del usuario.
     */
    public int getIdUsuario() {
        return idUsuario;
    }
    /**
     * Establece el identificador del usuario.
     * @param idUsuario Nuevo id a asignar.
     * @return true si la asignación fue exitosa.
     */
    public boolean setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
        return this.idUsuario == idUsuario;
    }
    /**
     * Obtiene el cambio aplicado a la racha.
     * @return Entero con la variación de racha.
     */
    public int getCambioRacha() {
        return cambioRacha;
    }
    /**
     * Establece el cambio a aplicar en la racha.
     * @param cambioRacha Valor del cambio de racha.
     * @return true si la asignación fue exitosa.
     */
    public boolean setCambioRacha(int cambioRacha) {
        this.cambioRacha = cambioRacha;
        return this.cambioRacha == cambioRacha;
    }
    /**
     * Obtiene el cambio aplicado al contador de partidas ganadas.
     * @return Entero con la variación de partidas ganadas.
     */
    public int getCambioGanadas() {
        return cambioGanadas;
    }

    /**
     * Establece el cambio a aplicar en las partidas ganadas.
     * @param cambioGanadas Valor del cambio de partidas ganadas.
     * @return true si la asignación fue exitosa.
     */
    public boolean setCambioGanadas(int cambioGanadas) {
        this.cambioGanadas = cambioGanadas;
        return this.cambioGanadas == cambioGanadas;
    }

    /**
     * Obtiene el cambio aplicado al contador de partidas jugadas.
     * @return Entero con la variación de partidas jugadas.
     */
    public int getCambioJugadas() {
        return cambioJugadas;
    }
    /**
     * Establece el cambio a aplicar en las partidas jugadas.
     * @param cambioJugadas Valor del cambio de partidas jugadas.
     * @return true si la asignación fue exitosa.
     */
    public boolean setCambioJugadas(int cambioJugadas) {
        this.cambioJugadas = cambioJugadas;
        return this.cambioJugadas == cambioJugadas;
    }
    /**
     * Establece la racha actual del usuario tras la partida.
     * @param rachaActual Valor de la racha actual.
     * @return La misma instancia para permitir encadenamiento de llamadas.
     */
    public ResultadoPartida setRachaActual(int rachaActual) { 
        this.rachaActual = rachaActual; 
        return this; 
    }
    /**
     * Establece la racha máxima histórica del usuario.
     * @param rachaMax Valor de la racha máxima.
     * @return La misma instancia para permitir encadenamiento de llamadas.
     */
    public ResultadoPartida setRachaMax(int rachaMax) { 
        this.rachaMax = rachaMax; 
        return this; 
    }
    /**
     * Establece el total de partidas jugadas por el usuario.
     * @param partidasJugadas Total acumulado de partidas jugadas.
     * @return La misma instancia para permitir encadenamiento de llamadas.
     */
    public ResultadoPartida setPartidasJugadas(int partidasJugadas) { 
        this.partidasJugadas = partidasJugadas; 
        return this; 
    }
    /**
     * Establece el total de partidas ganadas por el usuario.
     * @param partidasGanadas Total acumulado de partidas ganadas.
     * @return La misma instancia para permitir encadenamiento de llamadas.
     */
    public ResultadoPartida setPartidasGanadas(int partidasGanadas) { 
        this.partidasGanadas = partidasGanadas; 
        return this; 
    }
}