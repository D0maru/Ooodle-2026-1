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

    public void setRachaActual(int rachaActual) { this.rachaActual = rachaActual; }
    public void setRachaMax(int rachaMax) { this.rachaMax = rachaMax; }
    public void setPartidasJugadas(int partidasJugadas) { this.partidasJugadas = partidasJugadas; }
    public void setPartidasGanadas(int partidasGanadas) { this.partidasGanadas = partidasGanadas; }
}