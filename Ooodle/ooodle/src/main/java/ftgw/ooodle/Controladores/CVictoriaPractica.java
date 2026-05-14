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

public class CVictoriaPractica {

    @FXML private Button botonRegresoLobby;
    @FXML private Button botonjugardenuevo;

    private boolean modoDificil;

    /** 
     * @param modoDificil
     */
    public void setModoDificil(boolean modoDificil) {
        this.modoDificil = modoDificil;
    }

    /** 
     * @param event
     */
    @FXML
    void Volver_Inicio(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight()));
            stage.setResizable(false);   
            stage.setMaximized(false);   
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo volver al Lobby. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /** 
     * @param event
     */
    @FXML
    void Volver_a_Jugar(ActionEvent event) {
        try {
            String fxml = modoDificil
                ? "JuegoPracticaDificil.fxml"
                : "JuegoPracticaFacil.fxml";

            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar el juego. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /** 
     * @param mensaje
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
