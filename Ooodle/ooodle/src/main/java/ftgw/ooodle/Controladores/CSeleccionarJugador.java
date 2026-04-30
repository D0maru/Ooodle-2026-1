package ftgw.ooodle.Controladores;

import Servicios.DAOUsuario;
import ftgw.ooodle.Modelo.SesionUsuario;
import ftgw.ooodle.Modelo.Usuario; // Importante importar tu modelo
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class CSeleccionarJugador {

    @FXML private Button botonAgregarJugador;
    @FXML private AnchorPane panelJugadores;
    private VBox listaJugadores;
    
    private DAOUsuario daoUsuario = new DAOUsuario();
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
        cargarJugadoresDesdeBD();
    }
    private void cargarJugadoresDesdeBD() {
        listaJugadores.getChildren().clear();
        // Ahora obtenemos objetos Usuario completos
        List<Usuario> usuarios = daoUsuario.cargarUsuarios(); 
        
        for (Usuario usuario : usuarios) {
            agregarFilaJugador(usuario);
        }
    }
    private void agregarFilaJugador(Usuario usuario) {
        Label lblNombre = new Label("👤  " + usuario.getNickname());
        lblNombre.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-cursor: hand;");
        HBox.setHgrow(lblNombre, Priority.ALWAYS);
        //Evento el cual deja ir al lobby al hacer click en el nombre del jugador
        lblNombre.setOnMouseClicked(e -> {
            System.out.println("Seleccionado: "+usuario.getNickname());
            SesionUsuario.getInstancia().setUsuarioActual(usuario);
            irAlLobby();
            
        });

        Button btnEliminar = new Button("🗑 Eliminar");
        btnEliminar.setStyle("-fx-background-color: #7a3a4a; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand;");
        btnEliminar.setOnAction(e -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar a " + usuario.getNickname() + "?", ButtonType.YES, ButtonType.NO);
            confirmacion.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) {
                    String resultado = daoUsuario.eliminarUsuario(usuario.id);
                    if (resultado.equals("Usuario eliminado correctamente")){
                        Alert Exito=new Alert(Alert.AlertType.INFORMATION,"El usuario"+usuario.getNickname()+" ha sido eliminado correctamente");
                        Exito.show();

                        cargarJugadoresDesdeBD();
                    }else{
                        Alert Error=new Alert(Alert.AlertType.ERROR,"Error al eliminar el usuario: "+resultado);
                        Error.show();
                    }    
                    }   
            });
        });
        HBox fila = new HBox(10, lblNombre, btnEliminar);
        fila.setStyle("-fx-alignment: center-left; -fx-padding: 8; -fx-background-color: #252550; -fx-background-radius: 8;");
        listaJugadores.getChildren().add(fila);
    }
    @FXML
    void agregarJugador(ActionEvent event) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Nuevo Jugador");
        dialogo.setHeaderText("Ingresa el nombre del jugador");
        dialogo.setContentText("Nombre:");

        dialogo.showAndWait().ifPresent(nombre -> {
            if (nombre != null && !nombre.trim().isEmpty()) {
                String Respuesta = daoUsuario.agregarUsuario(nombre);
                System.out.println(Respuesta);
                
                cargarJugadoresDesdeBD(); 
            }else{
                Alert alerta = new Alert(Alert.AlertType.WARNING, "El nombre no puede estar vacío.");
                alerta.show();
            }
        });
    }

    private void irAlLobby() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = (javafx.stage.Stage) panelJugadores.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}