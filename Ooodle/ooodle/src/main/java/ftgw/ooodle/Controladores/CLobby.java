package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import ftgw.ooodle.Servicios.DAOEstadisticas;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;
import ftgw.ooodle.Modelo.RelojDiario;

public class CLobby {

    @FXML private Label Label_Nickname;
    @FXML private Label Label_PorcentajeVictorias;
    @FXML private Label Label_RachaActual;
    @FXML private Label Label_RachaMaxima;
    @FXML private Label Label_idUsuario;
    @FXML private Label Reloj_Daily;
    @FXML private Label lblRango;

    @FXML private Button botonReglas;
    @FXML private Button btnDiario;
    @FXML private Button btnPractica;
    @FXML private Button BotonSJugador;

    @FXML private AnchorPane PanelInterfaz;
    @FXML private Circle circuloDificultad;

    private boolean modoDificil = false;

    private final DAOEstadisticas daoEstadisticas = new DAOEstadisticas();
    private RelojDiario relojDiario;
    private Timeline timelineReloj;

    private static final String RUTA_REGLAS = "/ftgw/ooodle/interfaces/Reglas.fxml";
    private static final String PRACTICA_FACIL = "/ftgw/ooodle/interfaces/JuegoPracticaFacil.fxml";
    private static final String PRACTICA_DIFICIL = "/ftgw/ooodle/interfaces/JuegoPracticaDificil.fxml";
    private static final String DIARIO_FACIL = "/ftgw/ooodle/interfaces/JuegoDiarioFacil.fxml";
    private static final String DIARIO_DIFICIL = "/ftgw/ooodle/interfaces/JuegoDiarioDificil.fxml";

    @FXML
    public void initialize() {
        if (SesionUsuario.haySesionActiva()) {
            cargarEstadisticas();
            iniciarReloj();
        } else {
            mostrarError("No hay ningún usuario en sesión. Por favor selecciona un jugador.");
        }
    }

    private void cargarEstadisticas() {
        Usuario usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
        ResultadoPartida stats;
        try {
            stats = daoEstadisticas.cargarEstadisticasAlLobby(usuarioActual.getId());
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
            return;
        }

        Label_Nickname.setText(usuarioActual.getNickname());
        Label_idUsuario.setText("ID: " + usuarioActual.getId());
        Label_RachaMaxima.setText(String.valueOf(stats.rachaMax));
        Label_RachaActual.setText(String.valueOf(stats.rachaActual));

        if (stats.partidasJugadas > 0) {
            double porcentaje = ((double) stats.partidasGanadas / stats.partidasJugadas) * 100;
            Label_PorcentajeVictorias.setText(String.format("%.1f%%", porcentaje));
        } else {
            Label_PorcentajeVictorias.setText("0%");
        }

        boolean puedeJugar = SesionUsuario.getInstancia().getUsuarioActual().isPuedeJugar();
        btnDiario.setDisable(!puedeJugar);
    }

    private void iniciarReloj() {
        boolean puedeJugar = SesionUsuario.getInstancia().getUsuarioActual().isPuedeJugar();
        relojDiario = new RelojDiario(puedeJugar);

        timelineReloj = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            Reloj_Daily.setText(relojDiario.getTiempoRestante());
            btnDiario.setDisable(!relojDiario.puedeJugar());
        }));
        timelineReloj.setCycleCount(Timeline.INDEFINITE);
        timelineReloj.play();
    }

    /** 
     * @param event
     */
    @FXML
    void traerReglas(ActionEvent event) {
        cargarVistaEnPanel(RUTA_REGLAS);
    }

    /** 
     * @param event
     */
    @FXML
    void cambiarDificultad(MouseEvent event) {
        modoDificil = !modoDificil;
        animarDificultad();
        actualizarTextoDificultad();
    }

    /** 
     * @param event
     */
    @FXML
    void abrirJPrac(ActionEvent event) {
        abrirJuego(event, PRACTICA_FACIL, PRACTICA_DIFICIL);
    }

    /** 
     * @param event
     */
    @FXML
    void abrirJdiario(ActionEvent event) {
        abrirJuego(event, DIARIO_FACIL, DIARIO_DIFICIL);
    }

    /** 
     * @param event
     * @param rutaFacil
     * @param rutaDificil
     */
    private void abrirJuego(ActionEvent event, String rutaFacil, String rutaDificil) {
        if (timelineReloj != null) timelineReloj.stop();
        String ruta = modoDificil ? rutaDificil : rutaFacil;
        cambiarEscenaCompleta(event, ruta);
    }

    /** 
     * @param ruta
     */
    private void cargarVistaEnPanel(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            PanelInterfaz.getChildren().setAll(root);
            ajustarAnchors(root);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista: " + ruta + "\n\nDetalle: " + e.getMessage());
        }
    }

    /** 
     * @param event
     * @param ruta
     */
    private void cambiarEscenaCompleta(ActionEvent event, String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = obtenerStage(event);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);   
            stage.setMaximized(false);
            stage.sizeToScene(); 
            stage.show();
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista: " + ruta + "\n\nDetalle: " + e.getMessage());
        }
    }

    private void animarDificultad() {
        TranslateTransition animation = new TranslateTransition(Duration.millis(200), circuloDificultad);
        animation.setToX(modoDificil ? 22 : 0);
        animation.play();
    }

    private void actualizarTextoDificultad() {
        lblRango.setText(modoDificil ? "Numeros del 1-12" : "Numeros del 1-9");
    }

    /** 
     * @param root
     */
    private void ajustarAnchors(Parent root) {
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
    }

    /** 
     * @param event
     * @return Stage
     */
    private Stage obtenerStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /** 
     * @param mensaje
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /** 
     * @param event
     */
    @FXML
    void volverUsuario(ActionEvent event) {
        cambiarEscenaCompleta(event, "/ftgw/ooodle/interfaces/SeleccionarJugador.fxml");
    }
}