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

public class CJuegoPracticaFacil {

    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    @FXML private Label res_1, res_2, res_3, res_4, res_5, res_6, cronometro;

    // Estilos originales para recuperar al reiniciar
    private static final String COLOR_COL_A = "-fx-background-color: #344E41; -fx-text-fill: white;"; 
    private static final String COLOR_COL_B = "-fx-background-color: #DAD7CD; -fx-text-fill: black;"; 
    private static final String COLOR_COL_C = "-fx-background-color: #A3B18A; -fx-text-fill: black;"; 
    private static final String COLOR_COL_D = "-fx-background-color: #588157; -fx-text-fill: white;"; 

    private static final String VERDE = "-fx-background-color: #00e676; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String GRIS = "-fx-background-color: #616161; -fx-text-fill: white; -fx-font-weight: bold;";

    private Juego juego;
    private CronometroJuego modeloCronometro;    
    private Timeline timeline;
    private TextField[][] matrizTablero;
    private Label[] listaResultados;
    private int columnaSeleccionada = 0;

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
        configurarTecladoFisico();
    }

    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    // Números 1-9
                    if (code.isDigitKey() && code != javafx.scene.input.KeyCode.DIGIT0 && code != javafx.scene.input.KeyCode.NUMPAD0) {
                        String tecla = code.toString();
                        String digit = tecla.substring(tecla.length() - 1);
                        procesarEntrada(Integer.parseInt(digit));
                        event.consume(); // <--- CRITICO: Evita el 11, 22, etc.
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

    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        
        int fila = juego.getIntentoActual();
        
        // Seteamos el número: setText reemplaza lo que haya, evitando duplicados visuales
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[fila][columnaSeleccionada].setText(String.valueOf(numero));
        
        // Avance automático de columna
        if (columnaSeleccionada < 3) {
            columnaSeleccionada++;
        }
    }

    @FXML 
    void ClickDel(ActionEvent e) {
        int fila = juego.getIntentoActual();
        
        // Si la celda actual está vacía, retrocedemos para borrar la anterior
        if (matrizTablero[fila][columnaSeleccionada].getText().isEmpty() && columnaSeleccionada > 0) {
            columnaSeleccionada--;
        }

        juego.borrarCelda(columnaSeleccionada); 
        matrizTablero[fila][columnaSeleccionada].clear();
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        int filaActual = juego.getIntentoActual();
        int[] colores = juego.validarIntento();
        
        if (colores == null) {
            mostrarAlerta("Atención", "Completa la fila.");
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
                // Solo la fila activa es "editable" visualmente
                matrizTablero[i][j].setDisable(i != filaActiva);
            }
        }
        columnaSeleccionada = 0; // Al pasar de fila, volvemos a la primera columna
    }

    private void configurarEventosTablero() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                TextField tf = matrizTablero[i][j];
                final int f = i; final int c = j;
                
                tf.setOnMouseClicked(event -> {
                    if (f == juego.getIntentoActual()) columnaSeleccionada = c;
                });

                tf.setEditable(false);         // <--- EVITA ESCRITURA DIRECTA
                tf.setFocusTraversable(false); // EVITA CURSOR PARPADEANDO
            }
        }
    }

    @FXML 
    void ClickRestart(ActionEvent e) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                TextField tf = matrizTablero[i][j];
                tf.clear();
                // Restaurar colores de las columnas
                if (j == 0) tf.setStyle(COLOR_COL_A);
                else if (j == 1) tf.setStyle(COLOR_COL_B);
                else if (j == 2) tf.setStyle(COLOR_COL_C);
                else tf.setStyle(COLOR_COL_D);
            }
        }
        iniciarNuevoJuego();
        // Reiniciar cronómetro si es necesario aquí
    }

    // Métodos de botones UI (Click1 a Click9) llaman a procesarEntrada...
    @FXML void Click1(ActionEvent e) { procesarEntrada(1); }
    @FXML void Click2(ActionEvent e) { procesarEntrada(2); }
    @FXML void Click3(ActionEvent e) { procesarEntrada(3); }
    @FXML void Click4(ActionEvent e) { procesarEntrada(4); }
    @FXML void Click5(ActionEvent e) { procesarEntrada(5); }
    @FXML void Click6(ActionEvent e) { procesarEntrada(6); }
    @FXML void Click7(ActionEvent e) { procesarEntrada(7); }
    @FXML void Click8(ActionEvent e) { procesarEntrada(8); }
    @FXML void Click9(ActionEvent e) { procesarEntrada(9); }

    private void iniciarNuevoJuego() {
        juego = new Juego(false);
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
    }

    private void configurarCronometro() {
        modeloCronometro = new CronometroJuego();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cronometro.setText(modeloCronometro.incrementoSegundos());
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

    private void cambiarEscena(ActionEvent evento, String fxml, boolean modoDificil) {
        try {
            detenerSistemas();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) cronometro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    @FXML
    private void volverAlLobby(ActionEvent event) {
        cambiarEscena(event, "Lobby.fxml", false);
    }
}