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
 * Controlador principal del modo Diario Difícil del juego Ooodle.
 * <p>
 * Esta clase administra la lógica de interacción entre la interfaz gráfica
 * y el modelo del juego durante una partida diaria en dificultad difícil.
 * </p>
 *
 * <p>
 * El controlador trabaja junto a las clases {@link Juego},
 * {@link Usuario}, {@link CronometroJuego},
 * {@link DAOEstadisticas}, {@link ResultadoPartida}
 * y {@link SesionUsuario} para gestionar la lógica de juego,
 * estadísticas del usuario y navegación entre escenas.
 * </p>
 */
public class CJuegoDiarioDificil {

    /** Campos de texto que representan las celdas del tablero (6 filas x 4 columnas). */
    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    
    /** Etiquetas para mostrar el resultado objetivo (target) en cada fila. */
    @FXML private Label res1, res2, res3, res4, res5, res6;
    
    /** Etiqueta visual para el tiempo transcurrido. */
    @FXML private Label cronometro;

    // Constantes de estilo para el diseño del tablero y retroalimentación
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
    
    /** Almacena temporalmente los dígitos ingresados para permitir números de dos cifras (hasta 12). */
    private String bufferTeclado = "";
    private Timeline timerBuffer;

    /**
     * Inicializa los componentes de la interfaz, configura el estado inicial del juego,
     * los eventos del tablero y el cronómetro.
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
        
        timerBuffer = new Timeline(new KeyFrame(Duration.millis(300), e -> procesarBuffer()));
    }

    /**
     * Configura el filtro de eventos para capturar la entrada del teclado físico
     * y redirigirla a la lógica del juego.
     */
    private void configurarTecladoFisico() {
        javafx.application.Platform.runLater(() -> {
            Scene scene = cronometro.getScene();
            if (scene != null) {
                scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                    javafx.scene.input.KeyCode code = event.getCode();
                    if (code.isDigitKey() || (code.ordinal() >= 25 && code.ordinal() <= 34)) {
                        String tecla = code.toString();
                        String digit = tecla.substring(tecla.length() - 1);
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

    /** 
     * Gestiona la acumulación de dígitos en el buffer para permitir números compuestos.
     * @param digito El dígito presionado en el teclado.
     */
    private void manejarEntradaTeclado(String digito) {
        timerBuffer.stop();
        bufferTeclado += digito;
        if (bufferTeclado.length() == 2) {
            procesarBuffer();
        } else {
            timerBuffer.playFromStart();
        }
    }

    /**
     * Procesa el contenido del buffer de teclado para determinar si se ingresó
     * un número válido (1-12) o si deben procesarse como dígitos individuales.
     */
    private void procesarBuffer() {
        if (bufferTeclado.isEmpty()) return;
        try {
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
        } catch (NumberFormatException e) { bufferTeclado = ""; }
    }

    /** 
     * Inserta un número en la celda actualmente seleccionada del tablero.
     * @param numero El valor numérico a colocar en la celda.
     */
    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        int fila = juego.getIntentoActual();
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[fila][columnaSeleccionada].setText(String.valueOf(numero));
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    /**
     * Elimina el contenido de la celda actual o retrocede a la anterior si la actual está vacía.
     */
    @FXML void ClickDel() {
        bufferTeclado = "";
        int fila = juego.getIntentoActual();
        if (matrizTablero[fila][columnaSeleccionada].getText().isEmpty() && columnaSeleccionada > 0) {
            columnaSeleccionada--;
        }
        juego.borrarCelda(columnaSeleccionada);
        matrizTablero[fila][columnaSeleccionada].clear();
    }

    /**
     * Reinicia el estado visual y lógico del juego para comenzar una nueva partida.
     * @param e Evento de acción del botón.
     */
    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        bufferTeclado = "";
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
     * Configura los estilos iniciales y los eventos de clic para cada celda del tablero.
     */
    private void configurarEventosTablero() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                TextField tf = matrizTablero[i][j];
                final int f = i; final int c = j;
                tf.setOnMouseClicked(e -> {
                    if (f == juego.getIntentoActual()) columnaSeleccionada = c;
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

    /**
     * Valida el intento actual del usuario, cambia los colores de las celdas
     * según el acierto y verifica las condiciones de victoria o derrota.
     * @param e Evento de acción del botón de verificación.
     */
    @FXML void ClickCheck(ActionEvent e) {
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
        if (juego.esGanador()) finalizarPartida(true);
        else if (juego.getIntentoActual() >= 6) finalizarPartida(false);
        else actualizarEstadoFilas();
    }

    /** 
     * Finaliza la partida, actualiza las estadísticas del usuario en la base de datos
     * y redirige a la escena correspondiente (Victoria/Derrota).
     * @param gano Indica si el usuario ganó la partida.
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
     * Crea una nueva instancia de juego con una ecuación aleatoria y actualiza la UI.
     */
    private void iniciarNuevoJuego() {
        Ecuacion ecuacion = new Ecuacion();
        juego = new Juego(true, ecuacion, usuarioActual);
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
    }

    /**
     * Habilita visualmente solo la fila actual en juego y deshabilita las demás.
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
     * Realiza la transición entre diferentes archivos FXML.
     * @param fxml Nombre del archivo FXML (con extensión) a cargar.
     */
    private void cambiarEscena(String fxml) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
            Stage stage = (Stage) cronometro.getScene().getWindow(); 
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            mostrarAlerta("Error de navegación", "No se pudo cambiar de pantalla: " + ex.getMessage());
        }
    }

    /** Redirige al usuario a la pantalla principal del lobby. */
    @FXML void volverAlLobby(ActionEvent event) { cambiarEscena("Lobby.fxml"); }

    /**
     * Inicializa y comienza el hilo del cronómetro que actualiza la UI cada segundo.
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

    /**
     * Detiene los procesos en segundo plano como el cronómetro y los timers de entrada.
     */
    private void detenerSistemas() { 
        if (timeline != null) timeline.stop(); 
        if (timerBuffer != null) timerBuffer.stop();
    }

    /** 
     * Despliega una ventana emergente de advertencia para informar al usuario.
     * @param titulo Encabezado de la alerta.
     * @param msg Cuerpo del mensaje informativo.
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // Métodos de eventos para los botones del teclado numérico en la interfaz (1-12)
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
}