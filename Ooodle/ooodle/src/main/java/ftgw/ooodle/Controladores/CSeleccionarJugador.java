package ftgw.ooodle.Controladores;

import Servicios.JugadorDAO;
import ftgw.ooodle.Modelo.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class CSeleccionarJugador {

    @FXML private Button botonAgregarJugador;
    @FXML private AnchorPane panelJugadores;
    private VBox listaJugadores;

    @FXML
    public void initialize() {
        listaJugadores = new VBox(8);
        listaJugadores.setStyle("-fx-padding: 10;");

        ScrollPane scroll = new ScrollPane(listaJugadores);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        AnchorPane.setTopAnchor(scroll, 0.0);
        AnchorPane.setBottomAnchor(scroll, 0.0);
        AnchorPane.setLeftAnchor(scroll, 0.0);
        AnchorPane.setRightAnchor(scroll, 0.0);

        panelJugadores.getChildren().add(scroll);

        // Cargar datos existentes de la base de datos
        cargarJugadoresDesdeBD();
    }

    private void cargarJugadoresDesdeBD() {
        listaJugadores.getChildren().clear();
        List<String> nombres = JugadorDAO.obtenerTodos();
        for (String nombre : nombres) {
            agregarFilaJugador(nombre);
        }
    }

    @FXML
    void agregarJugador(ActionEvent event) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nuevo Jugador");
        dialogo.setHeaderText("Ingresa el nombre del jugador");
        dialogo.setContentText("Nombre:");

        dialogo.showAndWait().ifPresent(nombre -> {
            if (nombre != null && !nombre.trim().isEmpty()) {
                String n = nombre.trim();
                // 1. Guardar en Base de Datos
                JugadorDAO.guardarJugador(n);
                // 2. Refrescar Interfaz
                agregarFilaJugador(n);
            }
        });
    }

    private void agregarFilaJugador(String nombre) {
        // 1. Crear el Label del nombre
        Label lblNombre = new Label("👤  " + nombre);
        lblNombre.setStyle("-fx-text-fill: #c0c0ee; -fx-font-size: 13px; -fx-cursor: hand;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        // EVENTO: Al hacer clic en el nombre, guardar sesión e ir al lobby
        lblNombre.setOnMouseClicked(e -> {
            System.out.println("Seleccionado: " + nombre);
            Sesion.setJugadorActual(nombre); 
            irAlLobby(); 
        });

        // 2. Crear el botón de eliminar (ESTO ES LO QUE TE FALTABA)
        Button btnEliminar = new Button("🗑 Eliminar");
        btnEliminar.setStyle("-fx-background-color: #7a3a4a; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");

        // Evento del botón eliminar
        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar a " + nombre + "?", ButtonType.YES, ButtonType.NO);
            confirmacion.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    Servicios.JugadorDAO.eliminarJugador(nombre);
                    // Necesitamos encontrar la fila para removerla
                    listaJugadores.getChildren().removeIf(node -> {
                        if (node instanceof HBox) {
                            HBox hb = (HBox) node;
                            return hb.getChildren().contains(btnEliminar);
                        }
                        return false;
                    });
                }
            });
        });

        // 3. Crear la fila (HBox) agregando AMBOS elementos
        HBox fila = new HBox(10, lblNombre, btnEliminar);
        fila.setStyle("-fx-alignment: center-left; -fx-padding: 8; -fx-background-color: #252550; -fx-background-radius: 8;");

        // 4. Agregar la fila al VBox principal
        listaJugadores.getChildren().add(fila);
    }
    private void irAlLobby() {
    try {
        // Reemplaza "Lobby.fxml" por el nombre real de tu archivo del lobby
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
        javafx.scene.Parent root = loader.load();
        
        javafx.stage.Stage stage = (javafx.stage.Stage) panelJugadores.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    } catch (java.io.IOException e) {
        System.err.println("Error al cargar el Lobby: " + e.getMessage());
        e.printStackTrace();
    }
}
}