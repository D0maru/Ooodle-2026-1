package ftgw.ooodle.Servicios;

import ftgw.ooodle.Modelo.Usuario;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Objeto de acceso a datos para la gestión de usuarios.
 * Permite cargar, agregar y eliminar usuarios de la base de datos,
 * así como evaluar su permiso de juego según la fecha de último acceso.
 */
public class DAOUsuario {
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
    public DAOUsuario() {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.pass = dotenv.get("DB_PASSWORD");
    }
    /**
     * Obtiene todos los usuarios registrados en la base de datos.
     * Evalúa para cada uno si puede jugar hoy según su fecha de último juego.
     * @return Lista de objetos Usuario con los datos cargados desde la base de datos.
     * @throws RuntimeException si ocurre un error al acceder a la base de datos.
     */
    public List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT Id, Nickname, Ultimojuego FROM Usuario";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("Id");
                String nombre = rs.getString("Nickname");
                Date fechaBD = rs.getDate("UltimoJuego");
                boolean juegoHoy = compararFechas(fechaBD);
                Usuario u = new Usuario(id, nombre, juegoHoy);
                lista.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar los jugadores desde la base de datos: " + e.getMessage(), e);
        }
        return lista;
    }
    /**
     * Registra un nuevo usuario en la base de datos junto con su fila de estadísticas inicial.
     * Asigna la fecha de último juego en 1970-01-01 para que pueda jugar de inmediato.
     * @param nickname Nombre de usuario a registrar.
     * @return Mensaje de éxito con el nickname, o mensaje de error si falla la operación.
     */
    public String agregarUsuario(String nickname) {       
        String sql = "INSERT INTO Usuario (Nickname, UltimoJuego) VALUES (?, '1970-01-01')";
        String sqlEst = "INSERT INTO Estadisticas (idUsuario) VALUES (?)";

        try (Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nickname);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1); 
                try (PreparedStatement psEst = con.prepareStatement(sqlEst)) {
                    psEst.setInt(1, idGenerado);
                    psEst.executeUpdate();
                }
            }
            return "Usuario " + nickname + " agregado correctamente.";
            
        } catch (SQLException e) {
            return "Error al guardar el usuario: " + e.getMessage();
        }
    }
    /**
     * Elimina un usuario de la base de datos por su identificador.
     * @param id Identificador del usuario a eliminar.
     * @return Mensaje de éxito si el usuario fue eliminado,
     *         mensaje de advertencia si no se encontró el id,
     *         o mensaje de error si falla la operación.
     */
    public String eliminarUsuario(int id) {
        String sql = "DELETE FROM Usuario WHERE Id = ?";

        try (Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                return "Usuario eliminado correctamente";
            } else {
                return "No se encontró el usuario con ese ID.";
            }
            
        } catch (SQLException e) {
            return "Error al eliminar: " + e.getMessage();
        }
    }

    /**
     * Determina si el usuario puede jugar hoy comparando la fecha de su último juego con la actual.
     * @param fechaBd Fecha del último juego almacenada en la base de datos; null si nunca ha jugado.
     * @return true si nunca ha jugado o si su último juego fue antes de hoy; false si ya jugó hoy.
     */
    public boolean compararFechas(Date fechaBd) {
        if (fechaBd == null) return true; 
        Date fechaActual = new Date(System.currentTimeMillis());
        return fechaBd.before(fechaActual) && !fechaBd.toString().equals(fechaActual.toString());
    }
}
