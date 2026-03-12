package ftgw.ooodle;

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
            // Intentamos cargar el FXML (usa "/" al inicio para mayor seguridad)
            URL fxmlLocation = getClass().getResource("lobby.fxml");
            
            if (fxmlLocation == null) {
                // Si el archivo no está en la misma carpeta, intentamos ruta absoluta
                fxmlLocation = getClass().getResource("lobby.fxml");
            }

            if (fxmlLocation == null) {
                throw new IOException("No se pudo encontrar lobby.fxml. Revisa la ubicación del archivo.");
            }

            Parent root = FXMLLoader.load(fxmlLocation);

            // Forma recomendada: Obtener el Stage desde el evento que disparó el botón
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cambiar de pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
