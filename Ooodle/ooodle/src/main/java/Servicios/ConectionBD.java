package Servicios;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionBD {
    private static Connection conexion = null;
    
    // Cargamos el archivo .env
    private static final Dotenv dotenv = Dotenv.load();
    
    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    private ConectionBD() {}

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("Error: Driver no encontrado");
            }
        }
        return conexion;
    }
}