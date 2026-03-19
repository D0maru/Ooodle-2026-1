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

public class CDerrotaDiario {

    @FXML
    private Button BotonRegreso;

    @FXML
    void Volver(ActionEvent event) {
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

}
