package ftgw.ooodle.Controladores;

import ftgw.ooodle.Modelo.*;
import ftgw.ooodle.Servicios.DAOEstadisticas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador principal para el modo Diario Fácil del juego Ooodle.
 * <p>
 * Gestiona la interfaz de usuario y la lógica de juego para la modalidad fácil,
 * donde los números permitidos son del 1 al 9. Coordina la validación de intentos,
 * el manejo del cronómetro y la persistencia de estadísticas.
 * </p>
 */
public class CJuegoDiarioFacil {

    /** Celdas del tablero organizadas en 6 intentos por 4 posiciones. */
    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    
    /** Etiquetas para mostrar el resultado objetivo de cada fila e indicador de tiempo. */
    @FXML private Label res1, res2, res3, res4, res5, res6, cronometro;

    // Constantes de estilo para las columnas y retroalimentación de aciertos
    private static final String COLOR_COL_A = "-fx-background-color: #344E41; -fx-text-fill: white;"; 
    private static final String COLOR_COL_B = "-fx-background-color: #DAD7CD; -fx-text-fill: black;"; 
    private static final String COLOR_COL_C = "-fx-background-color: #A3B18A; -fx-text-fill: black;"; 
    private static final String COLOR_COL_D = "-fx-background-color: #588157; -fx-text-fill: white;";

    private static final String VERDE = "-fx-background-color: #00e676; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String AMARILLO = "-fx-background-color: #ffd600; -fx-text-fill: black; -fx-font-weight: bold;";
    private static final String GRIS = "-fx-background-color: #616161; -fx-text-fill: white; -fx-font-weight: bold;";

    private Juego juego;
    private Usuario usuarioActual;
    private CronometroJuego modeloCronometro;
    private Timeline timeline;
    private DAOEstadisticas daoEstadisticas = new DAOEstadisticas();

    private TextField[][] matrizTablero;
    private Label[] listaResultados;
    private int columnaSeleccionada = 0;

    /**
     * Inicializa el controlador, vincula los componentes de la UI a las matrices lógicas
     * y arranca los sistemas de juego y cronómetro.
     */
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
    }

    /**
     * Configura la escucha de eventos del teclado físico (números 1-9, Backspace y Enter).
     */
    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();

                    // Detectar números 1-9 (Excluye el 0 en modo fácil)
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

    /** 
     * Procesa la entrada de un número y lo coloca en la celda activa del tablero.
     * @param numero El valor entero a ingresar en la posición actual.
     */
    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].setText(String.valueOf(numero));
        
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    /**
     * Borra el contenido de la celda actual y retrocede el cursor de selección.
     */
    @FXML void ClickDel() {
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[juego.getIntentoActual()][columnaSeleccionada].clear();
        if (columnaSeleccionada > 0) columnaSeleccionada--;
    }

    /**
     * Valida la fila actual contra la solución, aplica colores a las celdas
     * y gestiona el flujo de victoria, derrota o cambio de turno.
     */
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

    /** 
     * Detiene el juego, guarda los resultados en el sistema y cambia de escena.
     * @param gano true si el usuario ha ganado, false en caso contrario.
     */
    private void finalizarPartida(boolean gano) {
        detenerSistemas();
        if (usuarioActual != null) {
            int id = usuarioActual.getId();
            ResultadoPartida datos = gano ? new ResultadoPartida(id, 1, 1, 1) : new ResultadoPartida(id, -1, 0, 1);
            try {
                Usuario actualizado = daoEstadisticas.actualizarDatos(datos);
                SesionUsuario.getInstancia().setUsuarioActual(actualizado);
            } catch (RuntimeException ex) {
                mostrarAlerta("Error al guardar partida", ex.getMessage());
            }
        }
        cambiarEscena(gano ? "VictoriaDiario.fxml" : "DerrotaDiario.fxml");
    }

    /**
     * Genera una nueva instancia de juego con una ecuación de dificultad fácil.
     */
    private void iniciarNuevoJuego() {
        Ecuacion ecuacion = new Ecuacion();
        juego = new Juego(false, ecuacion, usuarioActual);
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
    }

    /**
     * Bloquea las filas anteriores y habilita únicamente la fila en curso.
     */
    private void actualizarEstadoFilas() {
        int filaActiva = juego.getIntentoActual();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                matrizTablero[i][j].setDisable(i != filaActiva);
            }
        }
        columnaSeleccionada = 0;
    }

    /** 
     * Carga y muestra una nueva interfaz basada en el archivo FXML proporcionado.
     * @param fxml Nombre del archivo de vista a cargar.
     */
    private void cambiarEscena(String fxml) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Stage stage = (Stage) cronometro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { mostrarAlerta("Error de navegación", "No se pudo cambiar de pantalla: " + ex.getMessage()); }
    }

    // --- Métodos de interacción de la UI ---

    @FXML void Click1() { procesarEntrada(1); }
    @FXML void Click2() { procesarEntrada(2); }
    @FXML void Click3() { procesarEntrada(3); }
    @FXML void Click4() { procesarEntrada(4); }
    @FXML void Click5() { procesarEntrada(5); }
    @FXML void Click6() { procesarEntrada(6); }
    @FXML void Click7() { procesarEntrada(7); }
    @FXML void Click8() { procesarEntrada(8); }
    @FXML void Click9() { procesarEntrada(9); }

    /** Ejecuta la lógica de comprobación de la fila. @param e Evento de acción. */
    @FXML void ClickCheck(ActionEvent e) { ejecutarValidacion(); }
    
    /** 
     * Reinicia la partida actual, limpiando el tablero y restaurando cronómetro.
     * @param e Evento de acción del botón reiniciar.
     */
    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        if (modeloCronometro != null) cronometro.setText(modeloCronometro.reiniciar());
        if (timeline != null) timeline.playFromStart();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                TextField tf = matrizTablero[i][j];
                tf.clear();
                if (j == 0) tf.setStyle(COLOR_COL_A);
                else if (j == 1) tf.setStyle(COLOR_COL_B);
                else if (j == 2) tf.setStyle(COLOR_COL_C);
                else tf.setStyle(COLOR_COL_D);
            }
        }
        iniciarNuevoJuego();
    }

    /**
     * Configura los controladores de clics en las celdas y los estilos por columna.
     */
    private void configurarEventosTablero() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                final int fila = i; final int col = j;
                TextField tf = matrizTablero[i][j];
                
                tf.setOnMouseClicked(e -> {
                    if (fila == juego.getIntentoActual()) columnaSeleccionada = col;
                });
                
                tf.setEditable(false);
                tf.setFocusTraversable(false);

                if (j == 0) tf.setStyle(COLOR_COL_A);
                else if (j == 1) tf.setStyle(COLOR_COL_B);
                else if (j == 2) tf.setStyle(COLOR_COL_C);
                else tf.setStyle(COLOR_COL_D);
            }
        }
    }

    /** Regresa a la pantalla del lobby. @param event Evento de acción. */
    @FXML void volverAlLobby(ActionEvent event) { cambiarEscena("Lobby.fxml"); }

    /**
     * Inicializa el cronómetro y define su comportamiento de actualización por segundo.
     */
    private void configurarCronometro() {
        modeloCronometro = new CronometroJuego();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            cronometro.setText(modeloCronometro.incrementoSegundos());
            if (modeloCronometro.esTiempoMaximo()) detenerSistemas();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /** Detiene el flujo del cronómetro. */
    private void detenerSistemas() { if (timeline != null) timeline.stop(); }

    /** 
     * Lanza un cuadro de diálogo informativo.
     * @param titulo Encabezado de la alerta.
     * @param msg Mensaje de detalle.
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}