package Servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.Usuario;
import io.github.cdimascio.dotenv.Dotenv;

public class DAOEstadisticas {
    private final String url;
    private final String user;
    private final String pass;

    public DAOEstadisticas() {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.pass = dotenv.get("DB_PASSWORD");
    }

    public Usuario actualizarDatos(ResultadoPartida resultado) {
        String sqlSelect = "SELECT u.Nickname, u.UltimoJuego, e.Racha_Actual, e.Racha_Max, e.P_Jugadas, e.P_Ganadas " +
                           "FROM Usuario u JOIN Estadisticas e ON u.Id = e.idUsuario " +
                           "WHERE u.Id = ?";
        
        String sqlUpdate = "UPDATE Estadisticas SET Racha_Actual = ?, Racha_Max = ?, P_Jugadas = ?, P_Ganadas = ? WHERE idUsuario = ?";
        
        Usuario usuarioActualizado = null;

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            int rachaAct = 0, rachaMax = 0, jugadas = 0, ganadas = 0;
            String nickname = "";
            Date ultimaFecha = null;

            // 1. Obtener datos actuales
            try (PreparedStatement psSel = con.prepareStatement(sqlSelect)) {
                psSel.setInt(1, resultado.idUsuario);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) {
                    nickname = rs.getString("Nickname");
                    ultimaFecha = rs.getDate("UltimoJuego");
                    rachaAct = rs.getInt("Racha_Actual");
                    rachaMax = rs.getInt("Racha_Max");
                    jugadas = rs.getInt("P_Jugadas");
                    ganadas = rs.getInt("P_Ganadas");
                }
            }
            if (resultado.cambioRacha == 1) {
                rachaAct += 1;
            } else if (resultado.cambioRacha == -1) {
                rachaAct = 0;
            }

            // Actualizar Racha Máxima si la actual la superó
            if (rachaAct > rachaMax) {
                rachaMax = rachaAct;
            }

            // Sumar partidas ganadas y jugadas
            ganadas += resultado.cambioGanadas;
            jugadas += resultado.cambioJugadas;

            // 3. Actualizar Base de Datos con los nombres exactos de las columnas
            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, rachaAct);
                psUpd.setInt(2, rachaMax);
                psUpd.setInt(3, jugadas);
                psUpd.setInt(4, ganadas);
                psUpd.setInt(5, resultado.idUsuario);
                psUpd.executeUpdate();
            }

            // 4. Retornar la nueva instancia de Usuario
            boolean puedeJugar = evaluarPermisoJuego(ultimaFecha); 
            usuarioActualizado = new Usuario(resultado.idUsuario, nickname, puedeJugar);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return usuarioActualizado;
    }

    // Método auxiliar para calcular el permiso basado en la fecha de la BD
    private boolean evaluarPermisoJuego(Date fechaDB) {
        if (fechaDB == null) return true;
        Date hoy = new Date(System.currentTimeMillis());
        // Solo puede jugar si la fecha guardada es estrictamente anterior a hoy
        return fechaDB.before(hoy) && !fechaDB.toString().equals(hoy.toString());
    }
}