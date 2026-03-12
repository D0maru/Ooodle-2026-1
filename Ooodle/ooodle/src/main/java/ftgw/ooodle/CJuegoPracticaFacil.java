package ftgw.ooodle;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CJuegoPracticaFacil {

    @FXML
    private Button BCheck;

    @FXML
    private Button BDel;

    @FXML
    private Button BT1;

    @FXML
    private Button BT2;

    @FXML
    private Button BT3;

    @FXML
    private Button BT4;

    @FXML
    private Button BT5;

    @FXML
    private Button BT6;

    @FXML
    private Button BT7;

    @FXML
    private Button BT8;

    @FXML
    private Button BT9;

    @FXML
    private Button BTRestart;

    @FXML
    void Click(ActionEvent event) {
         // Esto sirve para saber qué botón tocaste en la consola
        Button botonPresionado = (Button) event.getSource();
        System.out.println("Presionaste el botón: " + botonPresionado.getText());
    }
    @FXML
private Button BtnRPP; // Este debe coincidir con el fx:id de tu FXML

    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            // 1. Cargar el archivo FXML del lobby
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
            Parent root = loader.load();

            // 2. Obtener el Stage (la ventana) actual a partir del botón que se presionó
            Stage stage = (Stage) BtnRPP.getScene().getWindow();

            // 3. Cambiar la escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
