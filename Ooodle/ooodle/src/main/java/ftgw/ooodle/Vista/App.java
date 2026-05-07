package ftgw.ooodle.Vista;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // CORRECCIÓN: "SeleccionarJugador" con S mayúscula para que coincida con el archivo real
        scene = new Scene(loadFXML("Vista/SeleccionarJugador")); 
        stage.setScene(scene);
        stage.setResizable(false);   
        stage.setMaximized(false);   
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Construimos la ruta absoluta dentro del JAR
        String rutaCompleta = "/ftgw/ooodle/" + fxml + ".fxml";
        
        URL recurso = App.class.getResource(rutaCompleta);
        
        if (recurso == null) {
            throw new IOException("No se encontró el archivo FXML en la ruta: " + rutaCompleta 
                + ". Verifica que las mayúsculas y minúsculas sean idénticas al archivo real.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(recurso);
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
