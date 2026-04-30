package ftgw.ooodle.Modelo;

public class ResultadoPartida {
    public int idUsuario;
    public int cambioRacha;   // Se usa para actualizar la BD
    public int cambioGanadas; // Se usa para actualizar la BD
    public int cambioJugadas; 

    // NUEVOS CAMPOS: Para guardar los totales que vienen de la base de datos
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

    // SETTERS CORREGIDOS: Ahora apuntan a sus variables correspondientes
    public void setRachaActual(int rachaActual) { this.rachaActual = rachaActual; }
    public void setRachaMax(int rachaMax) { this.rachaMax = rachaMax; }
    public void setPartidasJugadas(int partidasJugadas) { this.partidasJugadas = partidasJugadas; }
    public void setPartidasGanadas(int partidasGanadas) { this.partidasGanadas = partidasGanadas; }
}