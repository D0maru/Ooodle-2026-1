package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.util.Stack;

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
 * Controlador de la vista de derrota en el modo práctica.
 * <p>
 * Esta clase administra las acciones disponibles cuando el usuario
 * pierde una partida en modo práctica, permitiendo regresar al Lobby
 * principal o iniciar una nueva partida.
 * </p>
 * 
 * <p>
 * También gestiona el modo de dificultad seleccionado y controla
 * la navegación entre escenas de la aplicación.
 * </p>
 * 
 */
public class CDerrotaPractica {

    /**
     * Clase utilitaria encargada de administrar un historial
     * de escenas mediante una estructura tipo pila.
     * <p>
     * Permite almacenar escenas visitadas para facilitar
     * futuras funcionalidades de navegación.
     * </p>
     */
    public static class SceneManager {

        /**
         * Pila que almacena el historial de escenas.
         */
        private static final Stack<Scene> historial = new Stack<>();

        /**
         * Agrega una escena al historial.
         *
         * @param scene escena que será almacenada
         */
        public static void push(Scene scene) { 
            historial.push(scene); 
        }

        /**
         * Obtiene y elimina la última escena almacenada.
         *
         * @return la escena anterior o {@code null} si el historial está vacío
         */
        public static Scene pop() { 
            return !historial.isEmpty() ? historial.pop() : null; 
        }

        /**
         * Verifica si existen escenas previas almacenadas.
         *
         * @return {@code true} si existe al menos una escena en el historial,
         *         {@code false} en caso contrario
         */
        public static boolean hasPrevious() { 
            return !historial.isEmpty(); 
        }
    }

    /**
     * Botón utilizado para regresar al Lobby principal.
     */
    @FXML 
    private Button botonRegresoLobby;

    /**
     * Botón utilizado para iniciar una nueva partida.
     */
    @FXML 
    private Button botonjugardenuevo;

    /**
     * Indica si la partida se encuentra en modo difícil.
     */
    private boolean modoDificil;

    /**
     * Define la dificultad de la partida.
     * <p>
     * Este valor determina qué vista del juego será cargada
     * al seleccionar la opción de volver a jugar.
     * </p>
     *
     * @param modoDificil {@code true} para modo difícil,
     *                    {@code false} para modo fácil
     */
    public void setModoDificil(boolean modoDificil) {
        this.modoDificil = modoDificil;
    }

    /**
     * Regresa al usuario a la vista principal del Lobby.
     * <p>
     * Mantiene el tamaño actual de la ventana y deshabilita
     * la opción de maximizar.
     * </p>
     *
     * <p>
     * Si ocurre un error durante la carga de la escena,
     * se mostrará una alerta informando el problema.
     * </p>
     *
     * @param event evento generado al presionar el botón de regreso
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
     * Reinicia la partida cargando nuevamente la vista correspondiente
     * según la dificultad seleccionada.
     * <p>
     * Si {@code modoDificil} es verdadero, se cargará la vista
     * del modo difícil; de lo contrario, se cargará la vista
     * del modo fácil.
     * </p>
     *
     * <p>
     * En caso de error durante la carga de la escena,
     * se mostrará una alerta al usuario.
     * </p>
     *
     * @param event evento generado al presionar el botón
     *              de volver a jugar
     */
    @FXML
    void Volver_a_Jugar(ActionEvent event) {
        try {
            String fxml = modoDificil
                ? "JuegoPracticaDificil.fxml"
                : "JuegoPracticaFacil.fxml";

            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight()));
            stage.setResizable(false);   
            stage.setMaximized(false);   
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar el juego. Intenta reiniciar la aplicación.\n\nDetalle: " + e.getMessage());
        }
    }

    /**
     * Muestra una ventana de alerta con información sobre un error.
     * <p>
     * Este método se utiliza para notificar problemas relacionados
     * con la navegación entre escenas de la aplicación.
     * </p>
     *
     * @param mensaje descripción detallada del error ocurrido
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de navegación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}