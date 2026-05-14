package ftgw.ooodle;

import ftgw.ooodle.Vista.App;

/**
 * Clase de lanzamiento técnica para la aplicación Ooodle.
 * <p>
 * El propósito de esta clase es servir como un punto de entrada (Entry Point) que no 
 * hereda de {@code javafx.application.Application}. Esto es una práctica común para 
 * evitar problemas de configuración de módulos en el Java Runtime Environment (JRE) 
 * al ejecutar archivos JAR que contienen librerías de JavaFX.
 * </p>
 */
public class Launcher {

    /** 
     * Método de inicio del sistema.
     * <p>
     * Delegar la ejecución al método {@code main} de la clase {@link App}. 
     * Al hacer esto, la JVM no busca los módulos de JavaFX en el arranque inicial, 
     * permitiendo que la aplicación se inicie correctamente incluso con una 
     * configuración de classpath tradicional.
     * </p>
     * @param args Argumentos de la línea de comandos pasados al iniciar el programa.
     */
    public static void main(String[] args) {
        // Redirección de la ejecución hacia la clase principal de la Vista
        App.main(args);
    }
}