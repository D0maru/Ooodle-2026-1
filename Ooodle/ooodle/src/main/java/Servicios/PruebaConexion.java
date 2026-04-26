package Servicios;

public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("--- Iniciando Prueba de Persistencia ---");
        UsuarioDAO dao = new UsuarioDAO();
        String nombre = "Jugador_Prueba";

        // 1. CREAR: Registramos el usuario
        dao.registrarNuevoUsuario(nombre);

        // 2. LEER: Obtenemos sus estadísticas actuales (estarán en 0)
        Estadisticas stats = dao.obtenerEstadisticas(nombre);
        System.out.println("Partidas antes: " + stats.partidasJugadas);

        // 3. MODIFICAR: Simulamos una victoria
        stats.partidasJugadas++;
        stats.partidasGanadas++;
        stats.rachaActual = 1;
        
        // 4. ACTUALIZAR: Guardamos los cambios en la BD
        dao.actualizarEstadisticas(nombre, stats);
        
        // 5. VERIFICAR: Volvemos a leer para confirmar
        Estadisticas statsFinales = dao.obtenerEstadisticas(nombre);
        System.out.println("Partidas después: " + statsFinales.partidasJugadas);
        System.out.println("--- Prueba Finalizada con Éxito ---");
    }
}
