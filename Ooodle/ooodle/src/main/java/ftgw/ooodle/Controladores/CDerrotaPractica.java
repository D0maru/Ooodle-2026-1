package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CDerrotaPractica {

    //Esta clase permitira volver a la interfaz anterior independendientemente cual sea
    public static class SceneManager {
    private static final Stack<Scene> historial = new Stack<>();

    public static void push(Scene scene) {
        historial.push(scene);
    }

    public static Scene pop() {
        if (!historial.isEmpty()) {
            return historial.pop();
        }
        return null;
    }

    public static boolean hasPrevious() {
        return !historial.isEmpty();
    }
}

    @FXML
    private Button botonRegresoLobby;

    @FXML
    private Button botonjugardenuevo;

    @FXML
    void Volver_Inicio(ActionEvent event) {
        try {
        Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Volver_a_Jugar(ActionEvent event) {
        //Es necesario buscar una manera de aclarar a cual pantalla de practica volver, la de dificultad facil o dificultad dificil
        try {
        Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/Vista/JuegoPracticaDificil.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
