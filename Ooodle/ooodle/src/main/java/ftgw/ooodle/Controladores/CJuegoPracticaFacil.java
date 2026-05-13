package ftgw.ooodle.Controladores;

import java.io.IOException;
import ftgw.ooodle.Modelo.CronometroJuego;
import ftgw.ooodle.Modelo.Ecuacion;
import ftgw.ooodle.Modelo.Juego;
import ftgw.ooodle.Modelo.ResultadoFila;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CJuegoPracticaFacil {

    // ── Estilos CSS de color (responsabilidad del controlador) ────────────
    private static final String ESTILO_VERDE    = "-fx-background-color: #00e676; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String ESTILO_AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-font-size: 16px;";
    private static final String ESTILO_GRIS     = "-fx-background-color: #616161; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 16px;";

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9;
    @FXML private Button BCheck, BDel, BTRestart, BotonLobby;

    @FXML private TextField a1, a2, a3, a4, a5, a6;
    @FXML private TextField b1, b2, b3, b4, b5, b6;
    @FXML private TextField c1, c2, c3, c4, c5, c6;
    @FXML private TextField d1, d2, d3, d4, d5, d6;

    @FXML private Label res_1, res_2, res_3, res_4, res_5, res_6;
    @FXML private Label cronometro;

    private Juego juego;
    private CronometroJuego modeloCronometro;
    private Timeline timeline;

    private TextField[][] tablero;
    private Label[]       resultados;
    private String[][]    estilosOriginales;

    @FXML
    public void initialize() {
        tablero = new TextField[][]{
            {a1, b1, c1, d1}, {a2, b2, c2, d2},
            {a3, b3, c3, d3}, {a4, b4, c4, d4},
            {a5, b5, c5, d5}, {a6, b6, c6, d6}
        };
        resultados = new Label[]{res_1, res_2, res_3, res_4, res_5, res_6};

        estilosOriginales = new String[6][4];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++)
                estilosOriginales[i][j] = tablero[i][j].getStyle();

        modeloCronometro = new CronometroJuego();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String tiempoActual = modeloCronometro.incrementoSegundos();
            cronometro.setText(tiempoActual);
            if (modeloCronometro.esTiempoMaximo()) {
                detenerSistemas();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
        juego = new Juego(false, usuario, new Ecuacion());

        int target = juego.GenerarNuevoJuego();
        actualizarResultados(target);

        juego.BloquearTodo();
        sincronizarBloqueoPorFila();
        juego.HabilitarFila(0);
        sincronizarHabilitacionFila(0);
    }

    private String detenerSistemas() {
        if (timeline != null) timeline.stop();
        return "Cronómetro pausado";
    }

    @FXML void Click1(ActionEvent e) { juego.EscribirNumero("1"); sincronizarCeldas(); }
    @FXML void Click2(ActionEvent e) { juego.EscribirNumero("2"); sincronizarCeldas(); }
    @FXML void Click3(ActionEvent e) { juego.EscribirNumero("3"); sincronizarCeldas(); }
    @FXML void Click4(ActionEvent e) { juego.EscribirNumero("4"); sincronizarCeldas(); }
    @FXML void Click5(ActionEvent e) { juego.EscribirNumero("5"); sincronizarCeldas(); }
    @FXML void Click6(ActionEvent e) { juego.EscribirNumero("6"); sincronizarCeldas(); }
    @FXML void Click7(ActionEvent e) { juego.EscribirNumero("7"); sincronizarCeldas(); }
    @FXML void Click8(ActionEvent e) { juego.EscribirNumero("8"); sincronizarCeldas(); }
    @FXML void Click9(ActionEvent e) { juego.EscribirNumero("9"); sincronizarCeldas(); }

    @FXML void ClickDel(ActionEvent e)     { juego.BorrarDigito(); sincronizarCeldas(); }

    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        cronometro.setText(modeloCronometro.reiniciar());
        timeline.playFromStart();

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++)
                tablero[i][j].setStyle(estilosOriginales[i][j]);

        int target = juego.ReiniciarJuego();
        actualizarResultados(target);
        sincronizarCeldas();
        sincronizarBloqueoPorFila();
        sincronizarHabilitacionFila(0);
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        ResultadoFila resultado = juego.ValidarFila();

        if (resultado.estado.equals("ERROR")) {
            mostrarError(resultado.mensajeError);
            return;
        }

        aplicarEstilosFila(resultado.filaValidada, resultado.estilosFila);
        sincronizarBloqueoPorFila();
        if (resultado.estado.equals("CONTINUA")) {
            sincronizarHabilitacionFila(juego.GetIntentoActual() - 1);
        }

        switch (resultado.estado) {
            case "GANASTE":
                cambiarEscena(e, "VictoriaPractica.fxml", false);
                break;
            case "PERDISTE":
                cambiarEscena(e, "DerrotaPractica.fxml", false);
                break;
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

            Stage stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) evento.getSource()).getScene().getWidth(), ((Node) evento.getSource()).getScene().getHeight()));
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void volverAlLobby(ActionEvent e) {
        try {
            detenerSistemas();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) e.getSource()).getScene().getWidth(), ((Node) e.getSource()).getScene().getHeight()));
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    // ── Helpers privados de sincronización UI ↔ Modelo ───────────────────

    private void sincronizarCeldas() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++)
                tablero[i][j].setText(juego.getValorCelda(i, j));
    }

    private void sincronizarBloqueoPorFila() {
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 4; j++) {
                boolean ed = juego.isEditable(i, j);
                tablero[i][j].setEditable(ed);
                tablero[i][j].setDisable(!ed);
            }
    }

    private void sincronizarHabilitacionFila(int fila) {
        for (int j = 0; j < 4; j++) {
            tablero[fila][j].setEditable(true);
            tablero[fila][j].setDisable(false);
        }
    }

    private void aplicarEstilosFila(int fila, String[] claves) {
        for (int j = 0; j < 4; j++) {
            String css;
            switch (claves[j]) {
                case Juego.COLOR_VERDE:    css = ESTILO_VERDE;    break;
                case Juego.COLOR_AMARILLO: css = ESTILO_AMARILLO; break;
                default:                   css = ESTILO_GRIS;     break;
            }
            tablero[fila][j].setStyle(css);
        }
    }

    private void actualizarResultados(int target) {
        String texto = String.valueOf(target);
        for (Label l : resultados) l.setText(texto);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}