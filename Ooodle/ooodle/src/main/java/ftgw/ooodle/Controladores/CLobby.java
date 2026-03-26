package ftgw.ooodle.Controladores;

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

    @FXML private BarChart<?, ?> HistoEstad;
    @FXML private AnchorPane PanelInterfaz;
    @FXML private Button botonReglas;
    @FXML private Circle circuloDificultad;
    @FXML private Label lblRango;

    private boolean modo12 = false;

    @FXML
    void traerReglas(ActionEvent event) {
        System.out.println("Intentando cargar reglas...");
        cambiarEscena("/ftgw/ooodle/Vista/Reglas.fxml");
    }

    @FXML
    void cambiarDificultad(MouseEvent event) {
        TranslateTransition animation = new TranslateTransition(Duration.millis(200), circuloDificultad);
        
        if (!modo12) {
            animation.setToX(22); 
            lblRango.setText("Numbers 1 to 12");
            modo12 = true;
        } else {
            animation.setToX(0);
            lblRango.setText("Numbers 1 to 9");
            modo12 = false;
        }
        animation.play(); 
    }

    @FXML
    void abrirJPrac(ActionEvent event) {
        // Selecciona la ruta dependiendo de modo12
        String ruta = modo12 ? "/ftgw/ooodle/Vista/JuegoPracticaDificil.fxml" 
                             : "/ftgw/ooodle/Vista/JuegoPracticaFacil.fxml";
        cambiarEscena(ruta);
    }

    @FXML
    void abrirJdiario(ActionEvent event) {
        // Selecciona la ruta dependiendo de modo12
        String ruta = modo12 ? "/ftgw/ooodle/Vista/JuegoDiarioDificil.fxml" 
                             : "/ftgw/ooodle/Vista/JuegoDiarioFacil.fxml";
        cambiarEscena(ruta);
    }

    // Método de apoyo para no repetir el código de carga de FXML
    private void cambiarEscena(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            
            // Limpia el panel y añade la nueva vista
            PanelInterfaz.getChildren().setAll(root);

            // Ajusta la vista a los bordes del AnchorPane
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            System.err.println("No se pudo cargar la vista: " + ruta);
            e.printStackTrace();
        }
    }
}

