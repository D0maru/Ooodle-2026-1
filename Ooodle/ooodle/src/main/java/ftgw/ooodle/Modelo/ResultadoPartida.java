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

    public int getIdUsuario() {
        return idUsuario;
    }

    public boolean setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
        return this.idUsuario == idUsuario;
    }

    public int getCambioRacha() {
        return cambioRacha;
    }

    public boolean setCambioRacha(int cambioRacha) {
        this.cambioRacha = cambioRacha;
        return this.cambioRacha == cambioRacha;
    }

    public int getCambioGanadas() {
        return cambioGanadas;
    }

    public boolean setCambioGanadas(int cambioGanadas) {
        this.cambioGanadas = cambioGanadas;
        return this.cambioGanadas == cambioGanadas;
    }

    public int getCambioJugadas() {
        return cambioJugadas;
    }

    public boolean setCambioJugadas(int cambioJugadas) {
        this.cambioJugadas = cambioJugadas;
        return this.cambioJugadas == cambioJugadas;
    }

    

    public ResultadoPartida setRachaActual(int rachaActual) { 
        this.rachaActual = rachaActual; 
        return this; 
    }

    public ResultadoPartida setRachaMax(int rachaMax) { 
        this.rachaMax = rachaMax; 
        return this; 
    }

    public ResultadoPartida setPartidasJugadas(int partidasJugadas) { 
        this.partidasJugadas = partidasJugadas; 
        return this; 
    }

    public ResultadoPartida setPartidasGanadas(int partidasGanadas) { 
        this.partidasGanadas = partidasGanadas; 
        return this; 
    }
}