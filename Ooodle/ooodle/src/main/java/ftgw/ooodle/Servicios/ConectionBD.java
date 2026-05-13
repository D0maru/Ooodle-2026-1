package ftgw.ooodle.Servicios;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class ConectionBD {
    private static Connection conexion = null;

    // Cargamos el archivo .env buscándolo en múltiples ubicaciones
    private static final Dotenv dotenv = cargarDotenv();

    private static Dotenv cargarDotenv() {
        // 1. Intentar desde el classpath (src/main/resources/.env)
        try {
            return Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception ignored) {}

        // 2. Intentar desde el directorio del .jar / working directory actual
        String[] candidatos = {
            ".",                          // working dir actual
            "Ooodle-2026-1",              // un nivel arriba si se lanza desde Repositorio Ooodle/
            "../Ooodle-2026-1",
            System.getProperty("user.dir") // por si acaso
        };

        for (String dir : candidatos) {
            Path ruta = Paths.get(dir, ".env");
            if (Files.exists(ruta)) {
                return Dotenv.configure()
                        .directory(dir)
                        .ignoreIfMissing()
                        .load();
            }
        }

        // 3. Fallback: variables de entorno del sistema operativo
        System.err.println("⚠️  No se encontró .env — usando variables de entorno del sistema.");
        return Dotenv.configure().ignoreIfMissing().systemProperties().load();
    }

    private static final String URL      = dotenv.get("DB_URL",      System.getenv("DB_URL"));
    private static final String USER     = dotenv.get("DB_USER",     System.getenv("DB_USER"));
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", System.getenv("DB_PASSWORD"));

    private ConectionBD() {}

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            if (URL == null || USER == null || PASSWORD == null) {
                throw new SQLException("❌ Faltan credenciales de BD. Verifica que exista el archivo .env con DB_URL, DB_USER y DB_PASSWORD.");
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("❌ Error: Driver MySQL no encontrado. Verifica la instalación del conector.");
            }
        }
        return conexion;
    }
}