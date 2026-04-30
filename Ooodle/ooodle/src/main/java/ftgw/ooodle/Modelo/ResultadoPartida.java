package ftgw.ooodle.Modelo;

public class ResultadoPartida {
    public int idUsuario;
    public int cambioRacha; // 1 o -1
    public int cambioGanadas; // 0 o 1
    public int cambioJugadas; // Siempre 1

    public ResultadoPartida(int idUsuario, int cambioRacha, int cambioGanadas, int cambioJugadas) {
        this.idUsuario = idUsuario;
        this.cambioRacha = cambioRacha;
        this.cambioGanadas = cambioGanadas;
        this.cambioJugadas = cambioJugadas;
    }
}