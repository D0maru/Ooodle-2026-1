package ftgw.ooodle.Controladores;

import ftgw.ooodle.Modelo.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class CJuegoPracticaFacil {

    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    @FXML private Label res_1, res_2, res_3, res_4, res_5, res_6, cronometro;

    private Juego juego;
    private CronometroJuego modeloCronometro;    
    private Timeline timeline;
    
    private TextField[][] matrizTablero;
    private Label[] listaResultados;
    private int columnaSeleccionada = 0;

    private static final String VERDE = "-fx-background-color: #00e676; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String GRIS = "-fx-background-color: #616161; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final String NORMAL = "-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ccc;";

    @FXML
    public void initialize() {  
        matrizTablero = new TextField[][]{
            {a1, b1, c1, d1}, {a2, b2, c2, d2}, {a3, b3, c3, d3},
            {a4, b4, c4, d4}, {a5, b5, c5, d5}, {a6, b6, c6, d6}
        };
        listaResultados = new Label[]{res_1, res_2, res_3, res_4, res_5, res_6};

        configurarEventosTablero();
        iniciarNuevoJuego();
        configurarCronometro();
        // Registro del teclado físico
        configurarTecladoFisico();
    }

    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    // Números 1-9 (Superior y Numpad)
                    if (code.isDigitKey() && code != javafx.scene.input.KeyCode.DIGIT0 && code != javafx.scene.input.KeyCode.NUMPAD0) {
                        String tecla = code.toString();
                        String digit = tecla.substring(tecla.length() - 1);
                        procesarEntrada(Integer.parseInt(digit));
                        event.consume();
                    } 
                    else if (code == javafx.scene.input.KeyCode.BACK_SPACE) {
                        ClickDel(null);
                        event.consume();
                    }
                    else if (code == javafx.scene.input.KeyCode.ENTER) {
                        ClickCheck(new ActionEvent(event.getSource(), null));
                        event.consume();
                    }
                });
            }
        });
    }

    private void iniciarNuevoJuego() {
        juego = new Juego(false);
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
    }

    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].setText(String.valueOf(numero));
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        int filaActual = juego.getIntentoActual();
        int[] colores = juego.validarIntento();
        
        if (colores == null) {
            mostrarAlerta("Atención", "Completa la fila sin repetir números.");
            return;
        }

        for (int j = 0; j < 4; j++) {
            if (colores[j] == 2) matrizTablero[filaActual][j].setStyle(VERDE);
            else if (colores[j] == 1) matrizTablero[filaActual][j].setStyle(AMARILLO);
            else matrizTablero[filaActual][j].setStyle(GRIS);
        }

        if (juego.esGanador()) {
            cambiarEscena(e, "VictoriaPractica.fxml", false);
        } else if (juego.getIntentoActual() >= 6) {
            cambiarEscena(e, "DerrotaPractica.fxml", false);
        } else {
            actualizarEstadoFilas();
        }
    }

    private void actualizarEstadoFilas() {
        int filaActiva = juego.getIntentoActual();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                matrizTablero[i][j].setDisable(i != filaActiva);
            }
        }
        columnaSeleccionada = 0;
    }

    // Botones UI vinculados a la lógica central
    @FXML void Click1(ActionEvent e) { procesarEntrada(1); }
    @FXML void Click2(ActionEvent e) { procesarEntrada(2); }
    @FXML void Click3(ActionEvent e) { procesarEntrada(3); }
    @FXML void Click4(ActionEvent e) { procesarEntrada(4); }
    @FXML void Click5(ActionEvent e) { procesarEntrada(5); }
    @FXML void Click6(ActionEvent e) { procesarEntrada(6); }
    @FXML void Click7(ActionEvent e) { procesarEntrada(7); }
    @FXML void Click8(ActionEvent e) { procesarEntrada(8); }
    @FXML void Click9(ActionEvent e) { procesarEntrada(9); }

    @FXML void ClickDel(ActionEvent e) {
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].clear();
        if (columnaSeleccionada > 0) columnaSeleccionada--;
    }

    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        cronometro.setText(modeloCronometro.reiniciar());
        timeline.playFromStart();
        for(TextField[] fila : matrizTablero) {
            for(TextField tf : fila) {
                tf.clear();
                tf.setStyle(NORMAL);
            }
        }
        iniciarNuevoJuego();
    }

    private void configurarEventosTablero() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                final int f = i; final int c = j;
                matrizTablero[i][j].setOnMouseClicked(event -> {
                    if (f == juego.getIntentoActual()) columnaSeleccionada = c;
                });
                matrizTablero[i][j].setDisable(true); // Se habilitan por actualizarEstadoFilas
            }
        }
    }

    private void cambiarEscena(ActionEvent evento, String fxml, boolean modoDificil) {
        try {
            detenerSistemas();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof CVictoriaPractica)
                ((CVictoriaPractica) controller).setModoDificil(modoDificil);
            else if (controller instanceof CDerrotaPractica)
                ((CDerrotaPractica) controller).setModoDificil(modoDificil);

            // Obtención robusta del Stage
            Stage stage;
            if (evento != null && evento.getSource() instanceof Node) {
                stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) cronometro.getScene().getWindow();
            }
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML void volverAlLobby(ActionEvent e) {
        cambiarEscena(e, "Lobby.fxml", false);
    }

    private void configurarCronometro() {
        modeloCronometro = new CronometroJuego();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cronometro.setText(modeloCronometro.incrementoSegundos());
            if (modeloCronometro.esTiempoMaximo()) detenerSistemas();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void detenerSistemas() { if (timeline != null) timeline.stop(); }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}