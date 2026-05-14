package ftgw.ooodle.Servicios;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Clase utilitaria que gestiona la conexión única a la base de datos.
 * Implementa el patrón Singleton para la conexión e intenta cargar
 * las credenciales desde un archivo .env, variables de entorno del sistema,
 * o directorios alternativos como respaldo.
 */
public class ConectionBD {

    /** Instancia única de la conexión activa a la base de datos. Null si aún no se ha conectado. */
    private static Connection conexion = null;

    /** Instancia de Dotenv cargada al iniciar la clase, usada para leer las credenciales. */
    private static final Dotenv dotenv = cargarDotenv();
    /**
     * Intenta cargar las variables de entorno desde un archivo .env.
     * Busca en el classpath, en directorios candidatos junto al ejecutable
     * y, como último recurso, usa las variables de entorno del sistema operativo.
     * @return Instancia de Dotenv con las variables cargadas.
     */
    private static Dotenv cargarDotenv() {
        // 1. Intentar cargar desde el classpath (src/main/resources/.env)
        try {
            return Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception ignored) {}

        // 2. Buscar el .env en directorios alternativos junto al .jar o directorio de trabajo
        String[] candidatos = {
            ".",                           // working dir actual
            "Ooodle-2026-1",               // un nivel arriba si se lanza desde Repositorio Ooodle/
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

        // 3. Fallback: usar variables de entorno del sistema operativo si no se encontró .env
        System.err.println("⚠️  No se encontró .env — usando variables de entorno del sistema.");
        return Dotenv.configure().ignoreIfMissing().systemProperties().load();
    }

    /** URL de conexión a la base de datos, obtenida desde .env o variables de entorno. */
    // URL

    /** Nombre de usuario de la base de datos. */
    // USER

    /** Contraseña de la base de datos. */
    // PASSWORD
    private static final String URL      = dotenv.get("DB_URL",      System.getenv("DB_URL"));
    private static final String USER     = dotenv.get("DB_USER",     System.getenv("DB_USER"));
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", System.getenv("DB_PASSWORD"));

    /** Constructor privado. Impide la instanciación directa de esta clase utilitaria. */
    private ConectionBD() {}
    /**
     * Obtiene la conexión activa a la base de datos, creándola si no existe o fue cerrada.
     * @return Objeto Connection listo para ejecutar consultas.
     * @throws SQLException si faltan credenciales o no se puede establecer la conexión.
     */
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