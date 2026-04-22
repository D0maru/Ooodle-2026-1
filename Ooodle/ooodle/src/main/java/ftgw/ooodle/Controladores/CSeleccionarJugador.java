package ftgw.ooodle.Controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class CSeleccionarJugador {

    @FXML private Button botonAgregarJugador;
    @FXML private Label labelTitulo;
    @FXML private AnchorPane panelJugadores;

    // VBox interno donde se apilan las filas de jugadores
    private VBox listaJugadores;

    @FXML
    public void initialize() {
        listaJugadores = new VBox(8);
        listaJugadores.setStyle("-fx-padding: 10;");

        ScrollPane scroll = new ScrollPane(listaJugadores);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Anclar el scroll a los 4 lados del panelJugadores
        AnchorPane.setTopAnchor(scroll, 0.0);
        AnchorPane.setBottomAnchor(scroll, 0.0);
        AnchorPane.setLeftAnchor(scroll, 0.0);
        AnchorPane.setRightAnchor(scroll, 0.0);

        panelJugadores.getChildren().add(scroll);
    }

    @FXML
    void agregarJugador(ActionEvent event) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nuevo Jugador");
        dialogo.setHeaderText("Ingresa el nombre del jugador");
        dialogo.setContentText("Nombre:");

        ButtonType guardar  = new ButtonType("Guardar",  ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().setAll(guardar, cancelar);

        dialogo.showAndWait().ifPresent(nombre -> {
            if (nombre == null || nombre.trim().isEmpty()) return;
            agregarFilaJugador(nombre.trim());
        });
    }

    private void agregarFilaJugador(String nombre) {
        Label lblNombre = new Label("👤  " + nombre);
        lblNombre.setStyle("-fx-text-fill: #c0c0ee; -fx-font-size: 13px;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        Button btnEliminar = new Button("🗑  Eliminar");
        btnEliminar.setStyle(
            "-fx-background-color: #7a3a4a; -fx-text-fill: white; " +
            "-fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        HBox fila = new HBox(10, lblNombre, btnEliminar);
        fila.setStyle(
            "-fx-alignment: center-left; -fx-padding: 10 14 10 14; " +
            "-fx-background-color: #252550; -fx-background-radius: 8;"
        );

        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Eliminar Jugador");
            confirmacion.setHeaderText("¿Seguro que quieres eliminar al jugador?");
            confirmacion.setContentText("\"" + nombre + "\" será eliminado de la lista.");

            ButtonType siEliminar = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.OK_DONE);
            ButtonType noCancelar = new ButtonType("Cancelar",     ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacion.getButtonTypes().setAll(siEliminar, noCancelar);

            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == siEliminar) {
                    listaJugadores.getChildren().remove(fila);
                }
            });
        });

        listaJugadores.getChildren().add(fila);
    }
}