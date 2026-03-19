package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CVictoriaPractica {

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