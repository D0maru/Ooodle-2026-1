package ftgw.ooodle.Servicios;

import ftgw.ooodle.Modelo.Usuario;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOUsuario {

    private final String url;
    private final String user;
    private final String pass;

    public DAOUsuario() {
        Dotenv dotenv = Dotenv.load();
        this.url = dotenv.get("DB_URL");
        this.user = dotenv.get("DB_USER");
        this.pass = dotenv.get("DB_PASSWORD");
    }
 // poner aque compare la fecha de el id con la fecha actual 
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
            e.printStackTrace();
        }
        return lista;
    }

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
            e.printStackTrace();
            return "Error al guardar el usuario: " + e.getMessage();
        }
    }

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
            e.printStackTrace();
            return "Error al eliminar: " + e.getMessage();
        }
    }

    
    public boolean compararFechas(Date fechaBd) {
        if(fechaBd == null)return true; 
        Date fechaActual = new Date(System.currentTimeMillis());
        return fechaBd.before(fechaActual) && !fechaBd.toString().equals(fechaActual.toString());
    }
} 
