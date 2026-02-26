package ftgw.ooodle;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class InterLobby {

    @FXML
    private BarChart<?, ?> HistoEstad;

    @FXML
    private AnchorPane PanelInterfaz;

    @FXML
    private Button botonReglas;

    @FXML
    void traerReglas(ActionEvent event) {
         try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("JueguitoRegla.fxml"));
        Parent root = loader.load();

        PanelInterfaz.getChildren().clear();
        PanelInterfaz.getChildren().add(root);

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
