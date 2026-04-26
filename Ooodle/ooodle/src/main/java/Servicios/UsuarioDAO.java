package Servicios;

import com.google.gson.Gson;
import java.sql.*;
import java.time.LocalDate;

public class UsuarioDAO {
    private final Gson gson = new Gson();

    // Reemplaza al antiguo 'cargar()'
    public Estadisticas obtenerEstadisticas(String nombre) {
        String sql = "SELECT Registro FROM Datos WHERE nombre = ?";
        try (Connection conn = ConectionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String json = rs.getString("Registro");
                Estadisticas stats = gson.fromJson(json, Estadisticas.class);
                
                // Aquí puedes mantener tu lógica de "Nuevo Día" si lo deseas
                verificarNuevoDia(stats);
                return stats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Estadisticas(); // Retorna stats en 0 si no existe
    }

    // Reemplaza al antiguo 'guardar()'
    public void actualizarEstadisticas(String nombre, Estadisticas stats) {
        // Lógica de porcentaje que tenías antes
        int n = stats.partidasJugadas;
        int w = stats.partidasGanadas;
        stats.porcentajeGanadas = (n == 0) ? 0 : (w * 100) / n;

        String sql = "UPDATE Datos SET Registro = ? WHERE nombre = ?";
        try (Connection conn = ConectionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, gson.toJson(stats));
            pstmt.setString(2, nombre);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void verificarNuevoDia(Estadisticas stats) {
        String hoy = LocalDate.now().toString();
        if (!hoy.equals(stats.ultimoDiaJugado)) {
            if (!stats.ultimoDiaJugado.isEmpty() && !stats.diarioJugadoHoy) {
                stats.rachaActual = 0;
            }
            stats.diarioJugadoHoy = false;
            stats.ultimoDiaJugado = hoy;
        }
    }
    public void registrarNuevoUsuario(String nombre) {
        // Creamos estadísticas vacías usando el constructor de tu clase
        Estadisticas statsIniciales = new Estadisticas();
        
        String sql = "INSERT INTO Datos (nombre, Registro) VALUES (?, ?)";

        try (Connection conn = ConectionBD.getConexion();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre);
            pstmt.setString(2, gson.toJson(statsIniciales)); // Mapeo a JSON

            pstmt.executeUpdate();
            System.out.println("¡Éxito! Usuario '" + nombre + "' creado.");

        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
        }
    }
}