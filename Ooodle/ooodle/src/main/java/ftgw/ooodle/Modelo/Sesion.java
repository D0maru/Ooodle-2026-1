package ftgw.ooodle.Modelo;

public class Sesion {
    private static String jugadorActual;

    public static void setJugadorActual(String nombre) {
        jugadorActual = nombre;
    }

    public static String getJugadorActual() {
        return jugadorActual;
    }
}
