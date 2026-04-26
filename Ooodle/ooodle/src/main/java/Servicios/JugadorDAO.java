package Servicios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    public static void guardarJugador(String nombre) {
        String sql = "INSERT INTO Datos (nombre) VALUES (?)"; 
        
        try (Connection conn = ConectionBD.getConexion()) {
            if (conn == null) return;
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nombre);
                pstmt.executeUpdate();
                System.out.println("✅ Guardado en BD: " + nombre);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al guardar jugador: " + e.getMessage());
            // SI AQUÍ DICE: "Field 'Registro' doesn't have a default value", 
            // es que la columna en MySQL sigue siendo NOT NULL.
        }
    }

    public static List<String> obtenerTodos() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM Datos";
        try (Connection conn = ConectionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener jugadores: " + e.getMessage());
        }
        return nombres;
    }

    public static void eliminarJugador(String nombre) {
        String sql = "DELETE FROM Datos WHERE nombre = ?";
        try (Connection conn = ConectionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            int filas = pstmt.executeUpdate();
            if(filas > 0) System.out.println("🗑️ Eliminado de BD: " + nombre);
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar jugador: " + e.getMessage());
        }
    }
}