package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CReglas {

    @FXML
    private Pane PanelBase;

    @FXML
    private Button botonVolver;

    @FXML
    private Label labeltitulo;

    @FXML
    void volverLobby(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) botonVolver.getScene().getWindow();
             Scene scene = new Scene(root, botonVolver.getScene().getWidth(), botonVolver.getScene().getHeight());
    stage.setScene(scene);
    stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
}