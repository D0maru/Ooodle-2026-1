package Servicios;
 
public class Estadisticas {
 
    public int partidasJugadas;
    public int partidasGanadas;
    public int rachaActual;
    public int rachaMaxima;
    public int porcentajeGanadas;
    public int[] indiceAdivinanza;
 
    public Estadisticas() {
        this.partidasJugadas   = 0;
        this.partidasGanadas   = 0;
        this.rachaActual       = 0;
        this.rachaMaxima       = 0;
        this.porcentajeGanadas = 0;
        this.indiceAdivinanza  = new int[6];
    }
}
 