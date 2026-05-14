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
 * Controlador de la vista de derrota del modo diario.
 * <p>
 * Esta clase administra la interacción del usuario cuando pierde
 * una partida diaria, permitiendo regresar al Lobby principal
 * de la aplicación.
 * </p>
 * 
 * <p>
 * Además, maneja posibles errores de navegación mostrando
 * mensajes de alerta al usuario.
 * </p>
 */
public class CDerrotaDiario {
    
    /**
     * Botón utilizado para regresar al Lobby principal.
     */
    @FXML
    private Button BotonRegreso;

    /**
     * Cambia la escena actual hacia la vista del Lobby.
     * <p>
     * Este método es ejecutado cuando el usuario presiona
     * el botón de regreso en la pantalla de derrota.
     * Mantiene el tamaño actual de la ventana y deshabilita
     * la opción de maximizar.
     * </p>
     * 
     * <p>
     * En caso de ocurrir un error durante la carga del archivo FXML,
     * se mostrará una alerta indicando el problema.
     * </p>
     *
     * @param event evento generado al presionar el botón de regreso
     */
    @FXML
    void Volver(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo volver al Lobby. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /**
     * Muestra una ventana de alerta con un mensaje de error.
     * <p>
     * Este método se utiliza para informar al usuario sobre
     * problemas relacionados con la navegación entre escenas.
     * </p>
     *
     * @param mensaje texto descriptivo del error ocurrido
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}