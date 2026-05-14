package ftgw.ooodle.Controladores;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controlador para la vista de Victoria en el modo de Juego Diario.
 * <p>
 * Esta clase se despliega cuando el usuario resuelve correctamente la ecuación del día.
 * Su función principal es proporcionar una confirmación visual del éxito y permitir 
 * la navegación de retorno al Lobby principal, gestionando la transición de la escena.
 * </p>
 */
public class CVictoriaDiario {
    
    /** Botón para efectuar la navegación de retorno al menú principal. */
    @FXML private Button botonRegresoLobby;

    /** 
     * Gestiona el evento de regreso al Lobby tras la victoria.
     * <p>
     * Carga de forma dinámica la interfaz del Lobby y ajusta el Stage actual 
     * manteniendo las dimensiones de ventana preferidas por el usuario.
     * </p>
     * @param event El evento de acción disparado por el botón de regreso.
     */
    @FXML
    void Volver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            
            // Obtención del Stage actual a través de la fuente del evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Creación de la escena preservando el tamaño actual para evitar saltos visuales
            Scene scene = new Scene(root, 
                ((Node) event.getSource()).getScene().getWidth(), 
                ((Node) event.getSource()).getScene().getHeight());
            
            stage.setScene(scene);
            stage.setResizable(false);   
            stage.setMaximized(false);   
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo volver al Lobby. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /** 
     * Despliega una alerta de error crítica si ocurre un fallo en la carga del FXML.
     * @param mensaje Descripción detallada del error de navegación.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}