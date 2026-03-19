package ftgw.ooodle;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CVictoriaDiario {

    @FXML
    private Button botonJugardenuevo;
  
    @FXML
    void jugar(ActionEvent event) {
        try {
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JuegoDiarioFacil.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage)botonJugardenuevo.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }

