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
import Servicios.Estadisticas;
import Servicios.EstadisticasService;
 
public class CLobby {
 
    @FXML private BarChart<?, ?> HistoEstad;
    @FXML private AnchorPane PanelInterfaz;
    @FXML private Button botonReglas;
    @FXML private Circle circuloDificultad;
    @FXML private Label lblRango;
 
    // Labels de estadísticas
    @FXML private Label D_partidasJugadas;
    @FXML private Label D_RachaMaxima;
    @FXML private Label D_RachaActual;
    @FXML private Label D_PorcentajeVictorias;
    @FXML private Label Ind_1;
    @FXML private Label Ind_2;
    @FXML private Label Ind_3;
    @FXML private Label Ind_4;
    @FXML private Label Ind_5;
    @FXML private Label Ind_6;
 
    private boolean modo12 = false;
 
    @FXML
    public void initialize() {
        Estadisticas stats = EstadisticasService.cargar();
        D_partidasJugadas.setText(String.valueOf(stats.partidasJugadas));
        D_RachaMaxima.setText(String.valueOf(stats.rachaMaxima));
        D_RachaActual.setText(String.valueOf(stats.rachaActual));
        D_PorcentajeVictorias.setText(stats.porcentajeGanadas + "%");
        if (stats.indiceAdivinanza != null && stats.indiceAdivinanza.length >= 6) {
            Ind_1.setText(String.valueOf(stats.indiceAdivinanza[0]));
            Ind_2.setText(String.valueOf(stats.indiceAdivinanza[1]));
            Ind_3.setText(String.valueOf(stats.indiceAdivinanza[2]));
            Ind_4.setText(String.valueOf(stats.indiceAdivinanza[3]));
            Ind_5.setText(String.valueOf(stats.indiceAdivinanza[4]));
            Ind_6.setText(String.valueOf(stats.indiceAdivinanza[5]));
        }
    }
 
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
        String ruta = modo12 ? "/ftgw/ooodle/Vista/JuegoPracticaDificil.fxml"
                             : "/ftgw/ooodle/Vista/JuegoPracticaFacil.fxml";
        cambiarEscena(ruta);
    }
 
    @FXML
    void abrirJdiario(ActionEvent event) {
        String ruta = modo12 ? "/ftgw/ooodle/Vista/JuegoDiarioDificil.fxml"
                             : "/ftgw/ooodle/Vista/JuegoDiarioFacil.fxml";
        cambiarEscena(ruta);
    }
 
    private void cambiarEscena(String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
 
            PanelInterfaz.getChildren().setAll(root);
 
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