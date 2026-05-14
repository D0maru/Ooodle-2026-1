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
 * Controlador para la vista de Victoria en el modo de Práctica.
 * <p>
 * Esta clase gestiona la pantalla que aparece tras ganar una partida de práctica. 
 * Ofrece al usuario la opción de regresar al Lobby o iniciar inmediatamente 
 * una nueva partida, preservando el nivel de dificultad seleccionado previamente.
 * </p>
 */
public class CVictoriaPractica {

    /** Botón para navegar de regreso al menú principal. */
    @FXML private Button botonRegresoLobby;
    
    /** Botón para reiniciar una sesión de práctica. */
    @FXML private Button botonjugardenuevo;

    /** Flag que determina si se debe reiniciar el juego en modo fácil o difícil. */
    private boolean modoDificil;

    /** 
     * Establece el modo de dificultad para la próxima partida.
     * Este método debe ser llamado por el controlador del juego antes de realizar la transición a esta vista.
     * @param modoDificil {@code true} para dificultad difícil, {@code false} para fácil.
     */
    public void setModoDificil(boolean modoDificil) {
        this.modoDificil = modoDificil;
    }

    /** 
     * Gestiona la navegación hacia el Lobby principal.
     * <p>
     * Carga el FXML correspondiente y ajusta la ventana actual (Stage) 
     * manteniendo las dimensiones de la escena anterior.
     * </p>
     * @param event El evento de acción disparado por el botón de regreso.
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
     * Permite al usuario jugar de nuevo instantáneamente.
     * <p>
     * Evalúa el valor de {@code modoDificil} para determinar si se debe cargar 
     * "JuegoPracticaDificil.fxml" o "JuegoPracticaFacil.fxml".
     * </p>
     * @param event El evento de acción disparado por el botón "Jugar de nuevo".
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
     * Despliega una alerta de error en caso de fallos en la carga de recursos FXML.
     * @param mensaje Descripción del error de navegación.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}