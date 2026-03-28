package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CJuegoDiarioDificil {

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12;
    @FXML private Button Bcheck, Bdel, Blooby, Brestart, BtnRPP;

    @FXML private TextField a1, a2, a3, a4, a5, a6;
    @FXML private TextField b1, b2, b3, b4, b5, b6;
    @FXML private TextField c1, c2, c3, c4, c5, c6;
    @FXML private TextField d1, d2, d3, d4, d5, d6;

    @FXML private Label res1, res2, res3, res4, res5, res6;
    @FXML private Label cronometro;

    // ===== VARIABLES DEL JUEGO =====
    private int target;
    private int intentoActual = 1;
    private int columnaActual = 0;
    private int[] solucion;

    private TextField[][] tablero;
    private Label[] resultados;

    // ===== COLORES =====
    private static final String VERDE    = "-fx-background-color: #00e676; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String GRIS     = "-fx-background-color: #616161; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 16px;";

    // ===== CRONÓMETRO =====
    private int segundosTranscurridos = 0;
    private Timeline timeline;

    @FXML
    public void initialize() {

        cronometro.setText("00:00");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            segundosTranscurridos++;
            actualizarLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // ===== TABLERO =====
        tablero = new TextField[][]{
            {a1, b1, c1, d1},
            {a2, b2, c2, d2},
            {a3, b3, c3, d3},
            {a4, b4, c4, d4},
            {a5, b5, c5, d5},
            {a6, b6, c6, d6}
        };

        resultados = new Label[]{res1, res2, res3, res4, res5, res6};

        generarNuevoJuego();
        bloquearTodo();
        habilitarFila(0);
    }

    // ===== GENERAR JUEGO =====
    private void generarNuevoJuego() {
        target = (int)(Math.random() * 149) - 7;
        solucion = ftgw.ooodle.Ecuacion.generateEquation(target, true);

        if (solucion != null) {
            System.out.printf(
                "SOLUCIÓN: %d * %d + %d - %d = %d%n",
                solucion[0], solucion[1], solucion[2], solucion[3], target
            );
        } else {
            System.out.println("No se encontró solución para: " + target);
        }

        for (Label l : resultados) {
            l.setText(String.valueOf(target));
        }
    }

    // ===== BOTONES NUMÉRICOS =====
    @FXML
    void ClickNumero(ActionEvent event) {
        Button boton = (Button) event.getSource();
        if (boton.getText().matches("([1-9]|1[0-2])")) {
            escribirNumero(boton.getText());
        }
    }

    // ===== CHECK =====
    @FXML
    void ClickCheck(ActionEvent event) {
        validarFila();
    }

    // ===== DEL =====
    @FXML
    void ClickDel(ActionEvent event) {
        borrarUltimo();
    }

    // ===== RESTART =====
    @FXML
    public void ClickRestart(ActionEvent event) {
        reiniciarJuego();
    }

    // ===== ESCRIBIR =====
    private void escribirNumero(String num) {
        if (intentoActual > 6) return;

        TextField campo = tablero[intentoActual - 1][columnaActual];
        if (!campo.isEditable()) return;

        if (campo.getText().isEmpty()) {
            campo.setText(num);
            columnaActual++;
            if (columnaActual > 3) columnaActual = 3;
        }
    }

    // ===== BORRAR =====
    private void borrarUltimo() {
        if (intentoActual > 6) return;
        if (!tablero[intentoActual - 1][0].isEditable()) return;

        if (columnaActual > 0) columnaActual--;
        tablero[intentoActual - 1][columnaActual].clear();
    }

    // ===== MOSTRAR ERROR =====
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ===== VALIDAR FILA =====
    private void validarFila() {
        try {
            String[] valores = new String[4];

            for (int i = 0; i < 4; i++) {
                valores[i] = tablero[intentoActual - 1][i].getText();

                if (valores[i] == null || valores[i].trim().isEmpty()) {
                    mostrarError("Debes completar todos los espacios.");
                    return;
                }

                if (!valores[i].matches("([1-9]|1[0-2])")) {
                    mostrarError("Solo se permiten números del 1 al 12.");
                    return;
                }
            }

            int a = Integer.parseInt(valores[0]);
            int b = Integer.parseInt(valores[1]);
            int c = Integer.parseInt(valores[2]);
            int d = Integer.parseInt(valores[3]);

            if (a == b || a == c || a == d ||
                b == c || b == d ||
                c == d) {
                mostrarError("No puedes usar números repetidos.");
                return;
            }

            int resultado = a * b + c - d;

            // ===== RETROALIMENTACIÓN DE COLORES =====
            aplicarColores(intentoActual - 1, new int[]{a, b, c, d});

            // 🎉 GANAR
            if (solucion != null &&
                a == solucion[0] &&
                b == solucion[1] &&
                c == solucion[2] &&
                d == solucion[3]) {
                cambiarEscena("VictoriaDiario.fxml");
                return;
            }

            if (resultado == target) {
                System.out.println("Correcto pero no es la solución propuesta");
            }

            deshabilitarFila(intentoActual - 1);
            intentoActual++;
            columnaActual = 0;

            if (intentoActual > 6) {
                cambiarEscena("DerrotaDiario.fxml");
                return;
            }

            habilitarFila(intentoActual - 1);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Ocurrió un error inesperado.");
        }
    }

    // ===== APLICAR COLORES =====
    private void aplicarColores(int fila, int[] intento) {
        for (int j = 0; j < 4; j++) {
            TextField celda = tablero[fila][j];

            if (intento[j] == solucion[j]) {
                celda.setStyle(VERDE);
            } else {
                boolean estaEnSolucion = false;
                for (int s = 0; s < 4; s++) {
                    if (intento[j] == solucion[s]) {
                        estaEnSolucion = true;
                        break;
                    }
                }
                celda.setStyle(estaEnSolucion ? AMARILLO : GRIS);
            }
        }
    }

    // ===== CAMBIO DE ESCENA =====
    private void cambiarEscena(String fxml) {
        try {
            if (timeline != null) { timeline.stop(); }

            var resource = getClass().getResource("/ftgw/ooodle/Vista/" + fxml);
            if (resource == null) {
                System.err.println("No se encontró el FXML: " + fxml);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) Bcheck.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== BLOQUEAR TODO =====
    private void bloquearTodo() {
        for (int i = 0; i < 6; i++) deshabilitarFila(i);
    }

    private void habilitarFila(int fila) {
        for (int j = 0; j < 4; j++) {
            tablero[fila][j].setEditable(true);
            tablero[fila][j].setDisable(false);
        }
    }

    private void deshabilitarFila(int fila) {
        for (int j = 0; j < 4; j++) {
            tablero[fila][j].setEditable(false);
            tablero[fila][j].setDisable(true);
        }
    }

    // ===== REINICIAR =====
    private void reiniciarJuego() {
        reiniciarCronometro();
        intentoActual = 1;
        columnaActual = 0;
        generarNuevoJuego();

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++) {
                tablero[i][j].clear();
                tablero[i][j].setStyle(""); // limpiar colores
            }

        bloquearTodo();
        habilitarFila(0);
    }

    // ===== CRONÓMETRO =====
    private void actualizarLabel() {
        if (segundosTranscurridos >= 3600) {
            cronometro.setText("!Te demoraste mucho!");
            timeline.stop();
            return;
        }
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        cronometro.setText(String.format("%02d:%02d", minutos, segundos));
    }

    private void reiniciarCronometro() {
        segundosTranscurridos = 0;
        actualizarLabel();
        timeline.playFromStart();
    }

    // ===== VOLVER AL LOBBY =====
    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) Blooby.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
}