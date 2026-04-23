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

public class CVictoriaDiario {

    @FXML
    private Button botonRegresoLobby;

    @FXML
    void Volver(ActionEvent event) {
        try {
        Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, ((Node) event.getSource()).getScene().getWidth(), ((Node) event.getSource()).getScene().getHeight());
    stage.setScene(scene);
    stage.setMaximized(true);
        stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
