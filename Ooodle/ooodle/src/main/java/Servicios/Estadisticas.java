package Servicios;
 
public class Estadisticas {
 
    public int partidasJugadas;
    public int partidasGanadas;
    public int rachaActual;
    public int rachaMaxima;
    public int porcentajeGanadas;
    public int[] indiceAdivinanza;
    public boolean diarioJugadoHoy;
    /** Fecha ISO (yyyy-MM-dd) del último día en que se jugó el modo diario */
    public String ultimoDiaJugado;

 
    public Estadisticas() {
        this.partidasJugadas   = 0;
        this.partidasGanadas   = 0;
        this.rachaActual       = 0;
        this.rachaMaxima       = 0;
        this.porcentajeGanadas = 0;
        this.indiceAdivinanza  = new int[6];
        this.diarioJugadoHoy   = false;
        this.ultimoDiaJugado   = "";
    }
}
 