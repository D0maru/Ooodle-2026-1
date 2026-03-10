package ftgw.ooodle;

import java.io.IOException;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class CLobby {

    @FXML
    private BarChart<?, ?> HistoEstad;

    @FXML
    private AnchorPane PanelInterfaz;

    @FXML
    private Button botonReglas;

    @FXML
    void traerReglas(ActionEvent event) {
         try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Reglas.fxml"));
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
    @FXML
    private Circle circuloDificultad;

    @FXML
    private Label lblRango;

    private boolean modo12 = false;
    @FXML
    void cambiarDificultad(MouseEvent event) {
        // Creamos la animación (200 milisegundos de duración)
        TranslateTransition animation = new TranslateTransition(Duration.millis(200), circuloDificultad);
        //if para mover el circulo a la derecha o a la izquierda dependiendo del modo que escoja el usuario
        if (!modo12) {
            // Mover a la derecha para modo 1-12
            animation.setToX(22); 
            lblRango.setText("Numbers 1 to 12");
            modo12 = true;
        } else {
            // Volver a la izquierda para modo 1-9
            animation.setToX(0);
            lblRango.setText("Numbers 1 to 9");
            modo12 = false;
        }
        
        animation.play(); // Ejecuta el movimiento
    }

}
