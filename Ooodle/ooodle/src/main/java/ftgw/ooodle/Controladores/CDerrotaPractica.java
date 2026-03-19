package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CDerrotaPractica {

    @FXML
    private Button botonRegresoLobby;

    @FXML
    private Button botonjugardenuevo;

   @FXML
    void Volver(ActionEvent event) {
        try {
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) botonRegresoLobby.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void jugar(ActionEvent event) {
        try {
           
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JuegoPracticaFacil.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) botonjugardenuevo.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

