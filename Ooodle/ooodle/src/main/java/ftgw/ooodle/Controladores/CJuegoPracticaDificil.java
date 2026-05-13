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

public class CJuegoPracticaDificil {

    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    @FXML private Label res1, res2, res3, res4, res5, res6, cronometro;

    private Juego juego;
    private CronometroJuego modeloCronometro;
    private Timeline timeline;
    
    private TextField[][] matrizTablero;
    private Label[] listaResultados;
    private int columnaSeleccionada = 0;

    // --- VARIABLES DEL BUFFER (IGUAL QUE EN DIARIO) ---
    private String bufferTeclado = "";
    private Timeline timerBuffer;

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

        configurarEventosTablero();
        iniciarNuevoJuego();
        configurarCronometro();
        
        // --- INICIALIZACIÓN DEL TECLADO ---
        configurarTecladoFisico();
        timerBuffer = new Timeline(new KeyFrame(Duration.millis(300), e -> procesarBuffer()));
    }

    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    if (code.isDigitKey() || (code.ordinal() >= 25 && code.ordinal() <= 34)) { 
                        String digit = code.toString().substring(code.toString().length() - 1);
                        manejarEntradaTeclado(digit);
                        event.consume();
                    } 
                    else if (code == javafx.scene.input.KeyCode.BACK_SPACE) {
                        ClickDel();
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

    private void manejarEntradaTeclado(String digito) {
        timerBuffer.stop(); 
        bufferTeclado += digito;

        if (bufferTeclado.length() == 2) {
            procesarBuffer();
        } else {
            timerBuffer.playFromStart();
        }
    }

    private void procesarBuffer() {
        if (bufferTeclado.isEmpty()) return;
        
        int valor = Integer.parseInt(bufferTeclado);
        
        if (valor > 12) {
            int primerDigito = Character.getNumericValue(bufferTeclado.charAt(0));
            procesarEntrada(primerDigito);
            bufferTeclado = bufferTeclado.substring(1);
            procesarBuffer(); 
        } else {
            procesarEntrada(valor);
            bufferTeclado = "";
        }
    }

    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].setText(String.valueOf(numero));
        
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        int filaAValidar = juego.getIntentoActual();
        int[] colores = juego.validarIntento();
        
        if (colores == null) {
            mostrarAlerta("Fila incompleta", "Debes llenar todas las celdas y no repetir números.");
            return;
        }

        for (int j = 0; j < 4; j++) {
            if (colores[j] == 2) matrizTablero[filaAValidar][j].setStyle(VERDE);
            else if (colores[j] == 1) matrizTablero[filaAValidar][j].setStyle(AMARILLO);
            else matrizTablero[filaAValidar][j].setStyle(GRIS);
        }

        if (juego.esGanador()) {
            cambiarEscena(e, "VictoriaPractica.fxml", true);
        } else if (juego.getIntentoActual() >= 6) {
            cambiarEscena(e, "DerrotaPractica.fxml", true);
        } else {
            actualizarEstadoFilas();
        }
    }

    // Botones numéricos vinculados a procesarEntrada
    @FXML void Click1() { procesarEntrada(1); }
    @FXML void Click2() { procesarEntrada(2); }
    @FXML void Click3() { procesarEntrada(3); }
    @FXML void Click4() { procesarEntrada(4); }
    @FXML void Click5() { procesarEntrada(5); }
    @FXML void Click6() { procesarEntrada(6); }
    @FXML void Click7() { procesarEntrada(7); }
    @FXML void Click8() { procesarEntrada(8); }
    @FXML void Click9() { procesarEntrada(9); }
    @FXML void Click10() { procesarEntrada(10); }
    @FXML void Click11() { procesarEntrada(11); }
    @FXML void Click12() { procesarEntrada(12); }

    @FXML void ClickDel() {
        bufferTeclado = ""; 
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].clear();
        // En Práctica mantenemos tu lógica de retroceder columna si es necesario
        if (columnaSeleccionada > 0) columnaSeleccionada--;
    }

    private void iniciarNuevoJuego() {
        juego = new Juego(true);
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

    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        bufferTeclado = "";
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

            // Obtención segura del Stage
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

    private void detenerSistemas() { 
        if (timeline != null) timeline.stop(); 
        if (timerBuffer != null) timerBuffer.stop();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}