package ftgw.ooodle.Controladores;

import java.io.IOException;

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

import Servicios.DAOEstadisticas;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;
import ftgw.ooodle.Modelo.RelojDiario;



public class CLobby {

    // --- COMPONENTES UI ---
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

    @FXML private AnchorPane PanelInterfaz;
    @FXML private Circle circuloDificultad;

    // --- ESTADO ---
    private boolean modoDificil = false;

    // --- SERVICIOS ---
    private final DAOEstadisticas daoEstadisticas = new DAOEstadisticas();
    private RelojDiario relojDiario;

    // --- CONSTANTES ---
    private static final String RUTA_REGLAS = "/ftgw/ooodle/Vista/Reglas.fxml";
    private static final String PRACTICA_FACIL = "/ftgw/ooodle/Vista/JuegoPracticaFacil.fxml";
    private static final String PRACTICA_DIFICIL = "/ftgw/ooodle/Vista/JuegoPracticaDificil.fxml";
    private static final String DIARIO_FACIL = "/ftgw/ooodle/Vista/JuegoDiarioFacil.fxml";
    private static final String DIARIO_DIFICIL = "/ftgw/ooodle/Vista/JuegoDiarioDificil.fxml";

    // --- INICIALIZACIÓN ---
    @FXML
    public void initialize() {
        if(SesionUsuario.getInstancia().getUsuarioActual() != null){
            
            cargarEstadisticas();
            iniciarReloj();
        }else{
            System.err.println("No hay ningun usuario en sesion");
        }
    }

    private void cargarEstadisticas() {
        // 1. Obtenemos la referencia limpia del usuario
        Usuario usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
        
        // 2. Llamamos al DAO UNA SOLA VEZ. 
        // Recuerda que tu DAO ya tiene la línea que hace el .setPuedeJugar() internamente.
        ResultadoPartida stats = daoEstadisticas.cargarEstadisticasAlLobby(usuarioActual.getId());

        // 3. Seteamos los textos
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

        // 4. USAMOS EL VALOR RECIÉN ACTUALIZADO
        // Forzamos la lectura del objeto global por si acaso
        boolean puedeJugar = SesionUsuario.getInstancia().getUsuarioActual().isPuedeJugar();
        
        System.out.println("DEBUG: ¿Puede jugar según el objeto? " + puedeJugar); // Para que lo veas en consola
        
        btnDiario.setDisable(!puedeJugar);
    }

    private void iniciarReloj() {
        boolean puedeJugar = SesionUsuario.getInstancia().getUsuarioActual().isPuedeJugar();
        relojDiario = new RelojDiario(Reloj_Daily, btnDiario, puedeJugar);
        relojDiario.iniciar();
    }
    @FXML
    void traerReglas(ActionEvent event) {
        cargarVistaEnPanel(RUTA_REGLAS);
    }

    @FXML
    void cambiarDificultad(MouseEvent event) {
        modoDificil = !modoDificil;
        animarDificultad();
        actualizarTextoDificultad();
    }

    @FXML
    void abrirJPrac(ActionEvent event) {
        abrirJuego(event, PRACTICA_FACIL, PRACTICA_DIFICIL);
    }

    @FXML
    void abrirJdiario(ActionEvent event) {
        abrirJuego(event, DIARIO_FACIL, DIARIO_DIFICIL);
    }

    private void abrirJuego(ActionEvent event, String rutaFacil, String rutaDificil) {
        relojDiario.detener();
        String ruta = modoDificil ? rutaDificil : rutaFacil;
        cambiarEscenaCompleta(event, ruta);
    }

    private void cargarVistaEnPanel(String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            PanelInterfaz.getChildren().setAll(root);
            ajustarAnchors(root);
        } catch (IOException e) {
            manejarError("Error cargando vista", ruta, e);
        }
    }

    private void cambiarEscenaCompleta(ActionEvent event, String ruta) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(ruta));
            Stage stage = obtenerStage(event);

            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setResizable(false);   
            stage.setMaximized(false);   
            stage.show();

        } catch (IOException e) {
            manejarError("No se pudo cargar la vista", ruta, e);
        }
    }

    private void animarDificultad() {
        TranslateTransition animation = new TranslateTransition(Duration.millis(200), circuloDificultad);
        animation.setToX(modoDificil ? 22 : 0);
        animation.play();
    }

    private void actualizarTextoDificultad() {
        lblRango.setText(modoDificil ? "Numbers 1 to 12" : "Numbers 1 to 9");
    }

    private void ajustarAnchors(Parent root) {
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
    }

    private Stage obtenerStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void manejarError(String mensaje, String ruta, Exception e) {
        System.err.println(mensaje + ": " + ruta);
        e.printStackTrace();
    }
}