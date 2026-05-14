package ftgw.ooodle.Vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Clase principal de la aplicación que inicia el ciclo de vida de JavaFX.
 * <p>
 * Se encarga de configurar el escenario primario (Stage), establecer la escena inicial 
 * de selección de jugador y proporcionar métodos estáticos para la carga dinámica 
 * de archivos FXML dentro del paquete de recursos.
 * </p>
 */
public class App extends Application {

    /** Escena estática compartida para facilitar el intercambio de nodos raíz. */
    private static Scene scene;

    /** 
     * Punto de entrada principal para la aplicación JavaFX.
     * <p>
     * Configura el escenario inicial, deshabilita la capacidad de redimensionado 
     * y carga la vista de "SeleccionarJugador" como pantalla de bienvenida.
     * </p>
     * @param stage El escenario primario proporcionado por la plataforma.
     * @throws IOException Si el archivo FXML inicial no puede ser localizado o cargado.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Se establece la vista inicial de la aplicación
        scene = new Scene(loadFXML("interfaces/SeleccionarJugador")); 
        stage.setScene(scene);
        stage.setResizable(false);   
        stage.setMaximized(false);   
        stage.show();
    }

    /** 
     * Cambia el contenido de la escena actual reemplazando el nodo raíz (Root).
     * @param fxml El nombre del archivo FXML (sin extensión) a cargar.
     * @throws IOException Si el archivo especificado no existe o es ilegible.
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /** 
     * Localiza y carga un archivo FXML desde los recursos del proyecto.
     * <p>
     * Este método utiliza rutas absolutas basadas en el paquete raíz del proyecto
     * para asegurar la compatibilidad tanto en entorno de desarrollo como en el JAR compilado.
     * </p>
     * @param fxml Ruta relativa del archivo dentro del directorio de recursos (sin .fxml).
     * @return El objeto {@link Parent} que representa la jerarquía de la interfaz cargada.
     * @throws IOException Si el recurso es {@code null} o falla la lectura del archivo.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Construcción de la ruta absoluta interna
        String rutaCompleta = "/ftgw/ooodle/" + fxml + ".fxml";
        
        URL recurso = App.class.getResource(rutaCompleta);
        
        if (recurso == null) {
            throw new IOException("No se encontró el archivo FXML en la ruta: " + rutaCompleta 
                + ". Verifica que las mayúsculas y minúsculas sean idénticas al archivo real.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(recurso);
        return fxmlLoader.load();
    }

    /** 
     * Método principal que lanza la aplicación.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}