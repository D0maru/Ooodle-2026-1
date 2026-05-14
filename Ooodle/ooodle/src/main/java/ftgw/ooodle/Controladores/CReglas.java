package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controlador para la vista de Reglas del juego.
 * <p>
 * Esta clase gestiona la interacción en la pantalla informativa donde se explican 
 * las mecánicas de Ooodle, permitiendo al usuario visualizar las instrucciones 
 * y navegar de vuelta al Lobby principal de forma segura.
 * </p>
 */
public class CReglas {

    /** Contenedor principal de la interfaz de reglas. */
    @FXML private Pane PanelBase;
    
    /** Botón para efectuar la navegación de retorno. */
    @FXML private Button botonVolver;
    
    /** Etiqueta de encabezado de la vista. */
    @FXML private Label labeltitulo;

    /** 
     * Gestiona el cambio de escena para regresar al Lobby.
     * <p>
     * Intenta cargar el recurso FXML del Lobby y configurar la ventana actual (Stage)
     * manteniendo las dimensiones previas para una transición visual fluida.
     * </p>
     * @param event El evento de acción disparado por el botón volver.
     */
    @FXML
    void volverLobby(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Parent root = loader.load();
            
            // Obtención del Stage actual a través del nodo del botón
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            
            // Creación de la nueva escena preservando el tamaño actual de la ventana
            Scene scene = new Scene(root, botonVolver.getScene().getWidth(), botonVolver.getScene().getHeight());
            
            stage.setScene(scene);
            stage.setResizable(false);   
            stage.setMaximized(false);   
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo volver al Lobby. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /** 
     * Despliega un cuadro de diálogo de error en caso de fallos de E/S o navegación.
     * @param mensaje Descripción detallada del error ocurrido.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}