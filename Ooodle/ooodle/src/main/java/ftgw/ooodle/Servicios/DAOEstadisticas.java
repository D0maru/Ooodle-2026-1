package ftgw.ooodle.Servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
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

    /**
     * Actualiza la base de datos tras terminar una partida y devuelve el usuario actualizado.
     * Lanza RuntimeException si falla la BD, para que el controlador la muestre al jugador.
     */
    public Usuario actualizarDatos(ResultadoPartida resultado) {
        String sqlSelect = "SELECT u.Nickname, u.UltimoJuego, e.Racha_Actual, e.Racha_Max, e.P_Jugadas, e.P_Ganadas " +
                           "FROM Usuario u JOIN Estadisticas e ON u.Id = e.idUsuario " +
                           "WHERE u.Id = ?";
        
        String sqlUpdate = "UPDATE Estadisticas SET Racha_Actual = ?, Racha_Max = ?, P_Jugadas = ?, P_Ganadas = ? WHERE idUsuario = ?";
        String sqlUpdateFecha = "UPDATE Usuario SET UltimoJuego = CURRENT_DATE WHERE Id = ?";

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            con.setAutoCommit(false);
            
            int rachaAct = 0, rachaMax = 0, jugadas = 0, ganadas = 0;
            String nickname = "";

            try (PreparedStatement psSel = con.prepareStatement(sqlSelect)) {
                psSel.setInt(1, resultado.idUsuario);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) {
                    nickname = rs.getString("Nickname");
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

            if (rachaAct > rachaMax) rachaMax = rachaAct;
            ganadas += resultado.cambioGanadas;
            jugadas += resultado.cambioJugadas;

            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, rachaAct);
                psUpd.setInt(2, rachaMax);
                psUpd.setInt(3, jugadas);
                psUpd.setInt(4, ganadas);
                psUpd.setInt(5, resultado.idUsuario);
                psUpd.executeUpdate();
            }

            try (PreparedStatement psFecha = con.prepareStatement(sqlUpdateFecha)) {
                psFecha.setInt(1, resultado.idUsuario);
                psFecha.executeUpdate();
            }

            con.commit();
            return new Usuario(resultado.idUsuario, nickname, false);

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar los resultados de la partida: " + e.getMessage(), e);
        }
    }

    /**
     * Carga los datos necesarios para mostrar en el Lobby.
     * Lanza RuntimeException si falla la BD, para que el controlador la muestre al jugador.
     */
    public ResultadoPartida cargarEstadisticasAlLobby(int idUsuario) {
        String sql = "SELECT u.UltimoJuego, e.Racha_Actual, e.Racha_Max, e.P_Jugadas, e.P_Ganadas " +
                    "FROM Usuario u JOIN Estadisticas e ON u.Id = e.idUsuario " +
                    "WHERE u.Id = ?";
        
        ResultadoPartida resultado = new ResultadoPartida(idUsuario, 0, 0, 0);

        try (Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                resultado.setRachaActual(rs.getInt("Racha_Actual"));
                resultado.setRachaMax(rs.getInt("Racha_Max"));
                resultado.setPartidasJugadas(rs.getInt("P_Jugadas"));
                resultado.setPartidasGanadas(rs.getInt("P_Ganadas"));
                
                java.sql.Date ultimaFechaSQL = rs.getDate("UltimoJuego");
                boolean puedeJugar = evaluarPermisoJuego(ultimaFechaSQL);

                Usuario actual = SesionUsuario.getInstancia().getUsuarioActual();
                if (actual != null) {
                    actual.setPuedeJugar(puedeJugar);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar las estadísticas del jugador: " + e.getMessage(), e);
        }
        return resultado;
    }

    /** 
     * @param fechaDB
     * @return boolean
     */
    private boolean evaluarPermisoJuego(java.sql.Date fechaDB) {
        if (fechaDB == null) return true;
        LocalDate fechaUltimoJuego = fechaDB.toLocalDate();
        LocalDate hoy = LocalDate.now();
        return fechaUltimoJuego.isBefore(hoy);
    }
}
