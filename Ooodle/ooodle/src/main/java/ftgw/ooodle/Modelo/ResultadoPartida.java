package ftgw.ooodle.Modelo;

public class ResultadoPartida {
    public int idUsuario;
    public int cambioRacha;   
    public int cambioGanadas; 
    public int cambioJugadas; 
    public int rachaActual; 
    public int rachaMax;
    public int partidasJugadas;
    public int partidasGanadas;

    public ResultadoPartida(int idUsuario, int cambioRacha, int cambioGanadas, int cambioJugadas) {
        this.idUsuario = idUsuario;
        this.cambioRacha = cambioRacha;
        this.cambioGanadas = cambioGanadas;
        this.cambioJugadas = cambioJugadas;
    }

    /** 
     * @return int
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /** 
     * @param idUsuario
     * @return boolean
     */
    public boolean setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
        return this.idUsuario == idUsuario;
    }

    /** 
     * @return int
     */
    public int getCambioRacha() {
        return cambioRacha;
    }

    /** 
     * @param cambioRacha
     * @return boolean
     */
    public boolean setCambioRacha(int cambioRacha) {
        this.cambioRacha = cambioRacha;
        return this.cambioRacha == cambioRacha;
    }

    /** 
     * @return int
     */
    public int getCambioGanadas() {
        return cambioGanadas;
    }

    /** 
     * @param cambioGanadas
     * @return boolean
     */
    public boolean setCambioGanadas(int cambioGanadas) {
        this.cambioGanadas = cambioGanadas;
        return this.cambioGanadas == cambioGanadas;
    }

    /** 
     * @return int
     */
    public int getCambioJugadas() {
        return cambioJugadas;
    }

    /** 
     * @param cambioJugadas
     * @return boolean
     */
    public boolean setCambioJugadas(int cambioJugadas) {
        this.cambioJugadas = cambioJugadas;
        return this.cambioJugadas == cambioJugadas;
    }

    

    /** 
     * @param rachaActual
     * @return ResultadoPartida
     */
    public ResultadoPartida setRachaActual(int rachaActual) { 
        this.rachaActual = rachaActual; 
        return this; 
    }

    /** 
     * @param rachaMax
     * @return ResultadoPartida
     */
    public ResultadoPartida setRachaMax(int rachaMax) { 
        this.rachaMax = rachaMax; 
        return this; 
    }

    /** 
     * @param partidasJugadas
     * @return ResultadoPartida
     */
    public ResultadoPartida setPartidasJugadas(int partidasJugadas) { 
        this.partidasJugadas = partidasJugadas; 
        return this; 
    }

    /** 
     * @param partidasGanadas
     * @return ResultadoPartida
     */
    public ResultadoPartida setPartidasGanadas(int partidasGanadas) { 
        this.partidasGanadas = partidasGanadas; 
        return this; 
    }
}