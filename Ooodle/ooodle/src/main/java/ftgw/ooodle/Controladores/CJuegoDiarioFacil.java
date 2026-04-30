package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.sql.Date;
import Servicios.DAOEstadisticas;
import ftgw.ooodle.Modelo.CronometroJuego;
import ftgw.ooodle.Modelo.Juego;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CJuegoDiarioFacil {

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9;
    @FXML private Button Bcheck, Bdel, Blobby, Brestart;
    @FXML private AnchorPane PanelBase;

    @FXML private TextField a1, b1, c1, d1;
    @FXML private TextField a2, b2, c2, d2;
    @FXML private TextField a3, b3, c3, d3;
    @FXML private TextField a4, b4, c4, d4;
    @FXML private TextField a5, b5, c5, d5;
    @FXML private TextField a6, b6, c6, d6;

    @FXML private Label res1, res2, res3, res4, res5, res6;
    @FXML private Label cronometro;

    private Juego juego;
    private CronometroJuego cronometroJuego;
    
    // Lógica de Usuario y Persistencia
    private Usuario usuarioActual; 
    private DAOEstadisticas daoEstadisticas = new DAOEstadisticas();

    /**
     * Método fundamental para recibir al usuario desde el Lobby.
     * Sin esto, el juego daría error al intentar guardar estadísticas.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    @FXML
    public void initialize() {
        cronometroJuego = new CronometroJuego(cronometro);
        cronometroJuego.initialize();

        // Organización del tablero para la lógica de Juego.java
        TextField[][] tablero = {
            {a1, b1, c1, d1}, {a2, b2, c2, d2},
            {a3, b3, c3, d3}, {a4, b4, c4, d4},
            {a5, b5, c5, d5}, {a6, b6, c6, d6}
        };
        Label[] resultados = {res1, res2, res3, res4, res5, res6};

        juego = new Juego(false, tablero, resultados);
        juego.GenerarNuevoJuego();
        juego.BloquearTodo();
        juego.HabilitarFila(0);
    }

    // --- Controles del Teclado Numérico ---
    @FXML void Click1(ActionEvent e) { juego.EscribirNumero("1"); }
    @FXML void Click2(ActionEvent e) { juego.EscribirNumero("2"); }
    @FXML void Click3(ActionEvent e) { juego.EscribirNumero("3"); }
    @FXML void Click4(ActionEvent e) { juego.EscribirNumero("4"); }
    @FXML void Click5(ActionEvent e) { juego.EscribirNumero("5"); }
    @FXML void Click6(ActionEvent e) { juego.EscribirNumero("6"); }
    @FXML void Click7(ActionEvent e) { juego.EscribirNumero("7"); }
    @FXML void Click8(ActionEvent e) { juego.EscribirNumero("8"); }
    @FXML void Click9(ActionEvent e) { juego.EscribirNumero("9"); }

    @FXML void ClickDel(ActionEvent e) { juego.BorrarDigito(); }
    
    @FXML void ClickRestart(ActionEvent e) {
        cronometroJuego.ReiniciarCronometro();
        juego.ReiniciarJuego();
    }

    /**
     * Lógica principal de validación y guardado en Base de Datos.
     */
    @FXML 
    void ClickCheck(ActionEvent e) {
        String resultado = juego.ValidarFila();
        if (resultado == null) return;

        ResultadoPartida datosParaDAO;
        int id = usuarioActual.id; 

        if (resultado.equals("GANASTE")) {
            // Creamos el ticket de éxito: +1 racha, +1 ganada, +1 jugada
            datosParaDAO = new ResultadoPartida(id, 1, 1, 1);
            
            // Actualizamos en BD y recibimos el objeto Usuario fresco
            this.usuarioActual = daoEstadisticas.actualizarDatos(datosParaDAO);
            
            cambiarEscena(e, "VictoriaDiario.fxml");
            
        } else if (resultado.equals("PERDISTE")) {
            // Creamos el ticket de derrota: -1 resetea racha, 0 ganadas, +1 jugada
            datosParaDAO = new ResultadoPartida(id, -1, 0, 1);
            
            this.usuarioActual = daoEstadisticas.actualizarDatos(datosParaDAO);
            
            cambiarEscena(e, "DerrotaDiario.fxml");
        }
    }

    private void cambiarEscena(ActionEvent evento, String fxml) {
        try {
            cronometroJuego.DetenerCronometro();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/" + fxml));
            Parent root = loader.load();

            Object ControladorDestino = loader.getController();
            if(ControladorDestino instanceof CVictoriaDiario){ 
                ((CVictoriaDiario) ControladorDestino).setUsuario(this.usuarioActual);
            } else if (ControladorDestino instanceof CDerrotaDiario) {
                ((CDerrotaDiario) ControladorDestino).setUsuario(this.usuarioActual);
            }
            Stage stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void volverAlLobby(ActionEvent e) {
        try {
            cronometroJuego.DetenerCronometro();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            
            // Al volver al lobby, es vital pasar el usuario para que no se pierda la sesión
            Object proximoControlador = loader.getController();
            // Suponiendo que tu CLobby tiene un método setUsuario
            // ((CLobby) proximoControlador).setUsuario(this.usuarioActual);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}