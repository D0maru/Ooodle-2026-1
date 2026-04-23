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

    @FXML private Button botonRegresoLobby;
    @FXML private Button botonjugardenuevo;

    private boolean modoDificil;

    public void setModoDificil(boolean modoDificil) {
        this.modoDificil = modoDificil;
    }

    @FXML
    void Volver_Inicio(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight()));
        stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Volver_a_Jugar(ActionEvent event) {
        try {
            String fxml = modoDificil
                ? "JuegoPracticaDificil.fxml"
                : "JuegoPracticaFacil.fxml";

            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/Vista/" + fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}