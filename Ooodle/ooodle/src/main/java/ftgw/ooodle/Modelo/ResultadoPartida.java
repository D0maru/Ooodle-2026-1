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