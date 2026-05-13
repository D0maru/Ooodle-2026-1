package ftgw.ooodle.Controladores;

import ftgw.ooodle.Modelo.*;
import ftgw.ooodle.Servicios.DAOEstadisticas;
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

public class CJuegoDiarioFacil {

    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    @FXML private Label res1, res2, res3, res4, res5, res6, cronometro;

    private Juego juego;
    private Usuario usuarioActual;
    private CronometroJuego modeloCronometro;
    private Timeline timeline;
    private DAOEstadisticas daoEstadisticas = new DAOEstadisticas();

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
        listaResultados = new Label[]{res1, res2, res3, res4, res5, res6};
        this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();

        configurarEventosTablero();
        iniciarNuevoJuego();
        configurarCronometro();
        
        // Ejecutamos la configuración del teclado después de que la escena esté lista
        configurarTecladoFisico();
    }

    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    // Detectar números 1-9 (Teclado superior y numérico)
                    if (code.isDigitKey() && code != javafx.scene.input.KeyCode.DIGIT0 && code != javafx.scene.input.KeyCode.NUMPAD0) {
                        String tecla = code.toString();
                        String digit = tecla.substring(tecla.length() - 1);
                        procesarEntrada(Integer.parseInt(digit));
                        event.consume();
                    } 
                    else if (code == javafx.scene.input.KeyCode.BACK_SPACE) {
                        ClickDel();
                        event.consume();
                    }
                    else if (code == javafx.scene.input.KeyCode.ENTER) {
                        ejecutarValidacion();
                        event.consume();
                    }
                });
            }
        });
    }

    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].setText(String.valueOf(numero));
        
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    @FXML void ClickDel() {
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].clear();
        if (columnaSeleccionada > 0) columnaSeleccionada--;
    }

    private void ejecutarValidacion() {
        int filaAValidar = juego.getIntentoActual();
        int[] colores = juego.validarIntento();
        
        if (colores == null) {
            mostrarAlerta("Error", "Fila incompleta o números repetidos.");
            return;
        }

        for (int j = 0; j < 4; j++) {
            if (colores[j] == 2) matrizTablero[filaAValidar][j].setStyle(VERDE);
            else if (colores[j] == 1) matrizTablero[filaAValidar][j].setStyle(AMARILLO);
            else matrizTablero[filaAValidar][j].setStyle(GRIS);
        }

        if (juego.esGanador()) finalizarPartida(true);
        else if (juego.getIntentoActual() >= 6) finalizarPartida(false);
        else actualizarEstadoFilas();
    }

    private void finalizarPartida(boolean gano) {
        detenerSistemas();
        if (usuarioActual != null) {
            int id = usuarioActual.getId();
            ResultadoPartida datos = gano ? new ResultadoPartida(id, 1, 1, 1) : new ResultadoPartida(id, -1, 0, 1);
            Usuario actualizado = daoEstadisticas.actualizarDatos(datos);
            SesionUsuario.getInstancia().setUsuarioActual(actualizado);
        }
        cambiarEscena(gano ? "VictoriaDiario.fxml" : "DerrotaDiario.fxml");
    }

    private void iniciarNuevoJuego() {
        juego = new Juego(false);
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
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

    private void cambiarEscena(String fxml) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Stage stage = (Stage) cronometro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Botones UI
    @FXML void Click1() { procesarEntrada(1); }
    @FXML void Click2() { procesarEntrada(2); }
    @FXML void Click3() { procesarEntrada(3); }
    @FXML void Click4() { procesarEntrada(4); }
    @FXML void Click5() { procesarEntrada(5); }
    @FXML void Click6() { procesarEntrada(6); }
    @FXML void Click7() { procesarEntrada(7); }
    @FXML void Click8() { procesarEntrada(8); }
    @FXML void Click9() { procesarEntrada(9); }
    @FXML void ClickCheck(ActionEvent e) { ejecutarValidacion(); }
    
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
                final int fila = i; final int col = j;
                matrizTablero[i][j].setOnMouseClicked(e -> {
                    if (fila == juego.getIntentoActual()) columnaSeleccionada = col;
                });
                matrizTablero[i][j].setEditable(false);
            }
        }
    }

    @FXML void volverAlLobby(ActionEvent event) { cambiarEscena("Lobby.fxml"); }

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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}