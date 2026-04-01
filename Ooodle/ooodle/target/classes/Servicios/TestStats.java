package Servicios;

public class TestStats {
    public static void main(String[] args) {

        Estadisticas stats = EstadisticasService.cargar();

        stats.partidasJugadas++;
        stats.partidasGanadas++;

        EstadisticasService.guardar(stats);

        System.out.println("Jugadas: " + stats.partidasJugadas);
    }
}
