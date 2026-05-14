package ftgw.ooodle.Controladores;

import ftgw.ooodle.Servicios.DAOUsuario;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

/**
 * Controlador para la pantalla de Selección de Jugador.
 * <p>
 * Esta clase gestiona el ciclo de vida de los perfiles de usuario localmente. 
 * Permite listar los usuarios existentes desde la base de datos, crear nuevos perfiles 
 * con validación de longitud, eliminar registros y establecer el usuario activo 
 * en el singleton de sesión antes de entrar al juego.
 * </p>
 */
public class CSeleccionarJugador {

    /** Botón para disparar el diálogo de creación de usuario. */
    @FXML private Button botonAgregarJugador;
    
    /** Contenedor principal donde se inyecta la lista dinámica de jugadores. */
    @FXML private AnchorPane panelJugadores;
    
    /** Contenedor vertical que organiza visualmente las filas de jugadores. */
    private VBox listaJugadores;

    /** Acceso a los servicios de persistencia de usuarios. */
    private DAOUsuario daoUsuario = new DAOUsuario();

    /**
     * Inicializa la interfaz de selección. 
     * Configura el componente de desplazamiento (ScrollPane) y carga la lista
     * inicial de usuarios registrados en el sistema.
     */
    @FXML
    public void initialize() {
        listaJugadores = new VBox(8);
        listaJugadores.setStyle("-fx-padding: 10;");

        ScrollPane scroll = new ScrollPane(listaJugadores);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Anclaje del scroll para que ocupe todo el espacio disponible
        AnchorPane.setTopAnchor(scroll, 0.0);
        AnchorPane.setBottomAnchor(scroll, 0.0);
        AnchorPane.setLeftAnchor(scroll, 0.0);
        AnchorPane.setRightAnchor(scroll, 0.0);
        
        panelJugadores.getChildren().add(scroll);
        cargarJugadoresDesdeBD();
    }

    /**
     * Recupera la lista de usuarios mediante el DAO y refresca la interfaz visual.
     * En caso de error en la conexión o consulta, despliega una alerta informativa.
     */
    private void cargarJugadoresDesdeBD() {
        listaJugadores.getChildren().clear();
        List<Usuario> usuarios;
        try {
            usuarios = daoUsuario.cargarUsuarios();
        } catch (RuntimeException e) {
            mostrarError("Algo salió mal al cargar los jugadores.");
            return;
        }
        for (Usuario usuario : usuarios) {
            agregarFilaJugador(usuario);
        }
    }

    /** 
     * Crea y añade un componente visual (HBox) por cada usuario a la lista.
     * <p>
     * Cada fila incluye el nombre del jugador (seleccionable) y un botón de eliminación.
     * Al hacer clic en el nombre, se inicia la sesión y se navega al Lobby.
     * </p>
     * @param usuario El objeto de modelo {@link Usuario} a representar.
     */
    private void agregarFilaJugador(Usuario usuario) {
        Label lblNombre = new Label(usuario.getNickname());
        lblNombre.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-color: #588157; -fx-background-radius: 6; -fx-padding: 4 10 4 10;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);
        
        // Evento para seleccionar usuario e iniciar juego
        lblNombre.setOnMouseClicked(e -> {
            SesionUsuario.iniciarSesion(usuario);
            irAlLobby();
        });

        Button btnEliminar = new Button("🗑 Eliminar");
        btnEliminar.setStyle("-fx-background-color: #7a3a4a; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
        
        // Evento de eliminación con confirmación
        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar a " + usuario.getNickname() + "?", ButtonType.YES, ButtonType.NO);
            confirmacion.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    String resultado = daoUsuario.eliminarUsuario(usuario.id);
                    if (resultado.equals("Usuario eliminado correctamente")) {
                        Alert exito = new Alert(Alert.AlertType.INFORMATION,
                            "El usuario " + usuario.getNickname() + " ha sido eliminado correctamente.");
                        exito.show();
                        cargarJugadoresDesdeBD();
                    } else {
                        mostrarError("Algo salió mal al eliminar el usuario.");
                    }
                }
            });
        });

        HBox fila = new HBox(10, lblNombre, btnEliminar);
        fila.setStyle("-fx-alignment: center-left; -fx-padding: 8; -fx-background-color: #3a5a40; -fx-background-radius: 8;");        
        listaJugadores.getChildren().add(fila);
    }

    /** 
     * Despliega un diálogo de entrada de texto para registrar un nuevo jugador.
     * <p>
     * Incluye una validación en tiempo real que limita el nombre a 10 caracteres
     * y verifica que el campo no esté vacío antes de enviarlo al DAO.
     * </p>
     * @param event Evento de acción del botón "Agregar Jugador".
     */
    @FXML
    void agregarJugador(ActionEvent event) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nuevo Jugador");
        dialogo.setHeaderText("Ingresa el nombre del jugador");
        dialogo.setContentText("Nombre:");

        // Limitador de caracteres (max 10)
        TextField campoTexto = dialogo.getEditor();
        campoTexto.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().length() > 10) return null;
            return change;
        }));

        dialogo.showAndWait().ifPresent(nombre -> {
            if (nombre == null || nombre.trim().isEmpty()) {
                Alert alerta = new Alert(Alert.AlertType.WARNING, "El nombre no puede estar vacío.");
                alerta.show();
                return;
            }
            String respuesta = daoUsuario.agregarUsuario(nombre.trim());
            if (respuesta.startsWith("Error")) {
                mostrarError("Algo salió mal al agregar el jugador.");
            } else {
                cargarJugadoresDesdeBD();
            }
        });
    }

    /** 
     * Realiza la transición de escena hacia el Lobby principal tras seleccionar un usuario.
     */
    private void irAlLobby() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) panelJugadores.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            mostrarError("Algo salió mal al cargar el Lobby.");
        }
    }

    /** 
     * Centraliza la visualización de mensajes de error críticos para el usuario.
     * @param mensaje Descripción del problema ocurrido.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}