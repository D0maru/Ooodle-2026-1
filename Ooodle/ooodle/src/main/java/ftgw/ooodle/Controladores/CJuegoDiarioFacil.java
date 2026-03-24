package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; // Importante para obtener el Stage
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button; // Faltaba este import
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CJuegoDiarioFacil {

    @FXML
    private AnchorPane PanelBase;

    @FXML
    private Label letrero;

    @FXML
    private Button BtnRPP;

    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BtnRPP.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
