package ftgw.ooodle.Controladores;

import ftgw.ooodle.Modelo.*;
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
 * Controlador para el modo de Práctica en dificultad Difícil del juego Ooodle.
 * <p>
 * A diferencia de los modos diarios, esta clase permite al usuario jugar partidas
 * ilimitadas para practicar sin afectar sus estadísticas globales. Mantiene la 
 * complejidad de números del 1 al 12 y la lógica de entrada mediante buffer.
 * </p>
 */
public class CJuegoPracticaDificil {

    /** Componentes de texto del tablero (6x4). */
    @FXML private TextField a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4, a5, b5, c5, d5, a6, b6, c6, d6;
    
    /** Etiquetas para mostrar los resultados objetivos y el cronómetro. */
    @FXML private Label res1, res2, res3, res4, res5, res6, cronometro;

    // Estilos visuales para el feedback de las columnas y validaciones
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

    /** Buffer para gestionar la entrada de números de dos dígitos (10, 11, 12). */
    private String bufferTeclado = "";
    /** Temporizador para procesar automáticamente el buffer tras una breve pausa. */
    private Timeline timerBuffer;

    /**
     * Inicializa el estado de la vista, organiza la matriz de celdas y 
     * arranca los servicios de cronómetro y teclado.
     */
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
        configurarTecladoFisico();
        
        timerBuffer = new Timeline(new KeyFrame(Duration.millis(300), e -> procesarBuffer()));
    }

    /**
     * Configura los filtros de eventos para capturar teclas físicas.
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
     * Gestiona la acumulación de dígitos en el buffer de entrada.
     * @param digito Carácter numérico ingresado.
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
     * Analiza el buffer para determinar si el valor es un número válido (1-12)
     * o si debe descomponerse en dígitos individuales.
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
     * Inserta un valor numérico en la posición actual del tablero.
     * @param numero Valor a insertar.
     */
    private void procesarEntrada(int numero) {
        if (juego.getIntentoActual() >= 6) return;
        
        int fila = juego.getIntentoActual();
        juego.setNumeroEnCelda(columnaSeleccionada, numero);
        matrizTablero[fila][columnaSeleccionada].setText(String.valueOf(numero));
        
        if (columnaSeleccionada < 3) columnaSeleccionada++;
    }

    /** 
     * Realiza la comprobación matemática del intento actual y actualiza la UI
     * con los colores correspondientes.
     * @param e Evento de acción del botón.
     */
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

    /**
     * Borra el contenido de la celda actual. Incluye lógica de retroceso
     * si la celda ya está vacía.
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
     * Limpia el tablero y genera una nueva ecuación de práctica.
     * @param e Evento de acción.
     */
    @FXML void ClickRestart(ActionEvent e) {
        bufferTeclado = "";
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
        
        if (modeloCronometro != null) cronometro.setText(modeloCronometro.reiniciar());
        if (timeline != null) timeline.playFromStart();
        
        iniciarNuevoJuego();
    }

    /**
     * Instancia un nuevo objeto Juego en modo difícil para la sesión actual.
     */
    private void iniciarNuevoJuego() {
        Ecuacion ecuacion = new Ecuacion();
        juego = new Juego(true, ecuacion, SesionUsuario.getInstancia().getUsuarioActual());
        juego.generarNuevoJuego();
        for (Label l : listaResultados) l.setText(String.valueOf(juego.getTarget()));
        actualizarEstadoFilas();
    }

    /**
     * Deshabilita las filas que no corresponden al intento actual.
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
     * Establece los listeners de clic para permitir la selección manual de columnas.
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
            }
        }
    }

    // Handlers para los botones de la interfaz (1-12)
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

    /** Regresa al lobby principal. @param e Evento de acción. */
    @FXML void volverAlLobby(ActionEvent e) {
        cambiarEscena(e, "Lobby.fxml", false);
    }

    /** 
     * Gestiona la transición entre escenas de JavaFX y transfiere datos al controlador destino.
     * @param evento Evento que dispara el cambio.
     * @param fxml Nombre del archivo .fxml a cargar.
     * @param modoDificil Bandera para indicar la dificultad al controlador de destino.
     */
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

            Stage stage = (Stage) cronometro.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) { mostrarAlerta("Error de navegación", "No se pudo cambiar de pantalla: " + ex.getMessage()); }
    }

    /**
     * Inicializa y arranca el contador de tiempo.
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
     * Detiene los hilos de ejecución de cronómetros y buffers activos.
     */
    private void detenerSistemas() { 
        if (timeline != null) timeline.stop(); 
        if (timerBuffer != null) timerBuffer.stop();
    }

    /** 
     * Muestra una ventana de diálogo informativa.
     * @param titulo Título de la ventana.
     * @param msg Contenido del mensaje.
     */
    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}