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
/**
 * Objeto de acceso a datos para las estadísticas de los usuarios.
 * Gestiona la lectura y actualización de rachas, partidas jugadas
 * y partidas ganadas en la base de datos.
 */
public class DAOEstadisticas {
    /** URL de conexión a la base de datos. */
    private final String url;
    /** Nombre de usuario para autenticarse en la base de datos. */
    private final String user;
    /** Contraseña para autenticarse en la base de datos. */
    private final String pass;
    /**
     * Crea una instancia del DAO cargando las credenciales
     * de conexión desde el archivo .env.
     */
    public DAOEstadisticas() {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.pass = dotenv.get("DB_PASSWORD");
    }
    /**
     * Actualiza las estadísticas del usuario en la base de datos tras finalizar una partida.
     * Recalcula racha, racha máxima, partidas jugadas y ganadas, y actualiza la fecha de último juego.
     * @param resultado Objeto con los cambios generados por la partida.
     * @return Usuario actualizado con el id y nickname del jugador.
     * @throws RuntimeException si ocurre un error al acceder a la base de datos.
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
     * Carga las estadísticas del usuario desde la base de datos para mostrarlas en el Lobby.
     * También evalúa y actualiza en sesión si el usuario puede jugar hoy.
     * @param idUsuario Identificador del usuario a consultar.
     * @return ResultadoPartida con las estadísticas actuales del usuario.
     * @throws RuntimeException si ocurre un error al acceder a la base de datos.
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
     * Determina si el usuario puede jugar hoy comparando la fecha de su último juego con la fecha actual.
     * @param fechaDB Fecha del último juego almacenada en la base de datos; null si nunca ha jugado.
     * @return true si nunca ha jugado o si su último juego fue antes de hoy; false si ya jugó hoy.
     */
    private boolean evaluarPermisoJuego(java.sql.Date fechaDB) {
        if (fechaDB == null) return true;
        LocalDate fechaUltimoJuego = fechaDB.toLocalDate();
        LocalDate hoy = LocalDate.now();
        return fechaUltimoJuego.isBefore(hoy);
    }
}
