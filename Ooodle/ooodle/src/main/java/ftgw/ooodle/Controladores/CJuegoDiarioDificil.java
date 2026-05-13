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

public class CJuegoDiarioDificil {

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

    // --- NUEVAS VARIABLES PARA EL BUFFER ---
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
        this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();

        configurarEventosTablero();
        iniciarNuevoJuego();
        configurarCronometro();
        configurarTecladoFisico();
        
        // Inicializar el timer del buffer (espera 300ms por el segundo dígito)
        timerBuffer = new Timeline(new KeyFrame(Duration.millis(300), e -> procesarBuffer()));
    }

    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    if (code.isDigitKey() || (code.ordinal() >= 25 && code.ordinal() <= 34)) { // 0-9
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

    // --- LÓGICA DE UNIÓN DE DÍGITOS ---
    private void manejarEntradaTeclado(String digito) {
        timerBuffer.stop(); // Reinicia la espera si el usuario sigue escribiendo
        bufferTeclado += digito;

        // Si ya escribió dos números (ej: 1 y 2), procesar de inmediato
        if (bufferTeclado.length() == 2) {
            procesarBuffer();
        } else {
            // Esperar un poco a ver si escribe otro número
            timerBuffer.playFromStart();
        }
    }

    private void procesarBuffer() {
        if (bufferTeclado.isEmpty()) return;
        
        int valor = Integer.parseInt(bufferTeclado);
        
        // Si el valor es mayor a 12 (ej: 45), solo tomamos el primer dígito y descartamos el segundo
        // O lo limitamos al máximo permitido por tu juego (12)
        if (valor > 12) {
            int primerDigito = Character.getNumericValue(bufferTeclado.charAt(0));
            procesarEntrada(primerDigito);
            // El segundo dígito se procesa como una nueva entrada
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

    // Los botones de la pantalla siguen funcionando igual
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
        bufferTeclado = ""; // Limpiar buffer si borra
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].clear();
    }

    // ... (El resto de tus métodos: ClickCheck, finalizarPartida, etc., permanecen igual)
    @FXML
    void ClickCheck(ActionEvent e) {
        int[] colores = juego.validarIntento();
        if (colores == null) {
            mostrarAlerta("Error", "Fila incompleta o números repetidos.");
            return;
        }
        int filaFinalizada = juego.getIntentoActual() - 1;
        for (int j = 0; j < 4; j++) {
            if (colores[j] == 2) matrizTablero[filaFinalizada][j].setStyle(VERDE);
            else if (colores[j] == 1) matrizTablero[filaFinalizada][j].setStyle(AMARILLO);
            else matrizTablero[filaFinalizada][j].setStyle(GRIS);
        }
        if (juego.esGanador()) finalizarPartida(e, true);
        else if (juego.getIntentoActual() >= 6) finalizarPartida(e, false);
        else actualizarEstadoFilas();
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

    private void finalizarPartida(ActionEvent e, boolean gano) {
        detenerSistemas();
        if (usuarioActual != null) {
            int id = usuarioActual.getId();
            // Basado en los cortes académicos y metas de Sergio (2.7, 4.2), actualizamos estadísticas.
            ResultadoPartida datos = gano ? new ResultadoPartida(id, 1, 1, 1) : new ResultadoPartida(id, -1, 0, 1);
            Usuario actualizado = daoEstadisticas.actualizarDatos(datos);
            SesionUsuario.getInstancia().setUsuarioActual(actualizado);
        }
        // Llamamos al cambio de escena pasando el nombre del archivo
        cambiarEscena(gano ? "VictoriaDiario.fxml" : "DerrotaDiario.fxml");
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
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

    private void cambiarEscena(String fxml) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            
            // Forma segura de obtener el Stage sin importar si el evento vino del teclado o mouse
            Stage stage = (Stage) cronometro.getScene().getWindow(); 
            
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }

    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) { ex.printStackTrace(); }
    }
}