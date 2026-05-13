package ftgw.ooodle.Controladores;

import java.io.IOException;
import ftgw.ooodle.Servicios.DAOEstadisticas;
import ftgw.ooodle.Modelo.CronometroJuego;
import ftgw.ooodle.Modelo.Juego;
import ftgw.ooodle.Modelo.ResultadoPartida;
import ftgw.ooodle.Modelo.Usuario;
import ftgw.ooodle.Modelo.SesionUsuario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CJuegoDiarioDificil {

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12;
    @FXML private Button Bcheck, Bdel, Blooby, Brestart;

    @FXML private TextField a1, b1, c1, d1;
    @FXML private TextField a2, b2, c2, d2;
    @FXML private TextField a3, b3, c3, d3;
    @FXML private TextField a4, b4, c4, d4;
    @FXML private TextField a5, b5, c5, d5;
    @FXML private TextField a6, b6, c6, d6;

    @FXML private Label res1, res2, res3, res4, res5, res6;
    @FXML private Label cronometro;

    private Juego juego;
    private CronometroJuego modeloCronometro;
    private Timeline timeline;
    private DAOEstadisticas daoEstadisticas = new DAOEstadisticas();

    @FXML
    public void initialize() {
        modeloCronometro = new CronometroJuego();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String tiempoTexto = modeloCronometro.incrementoSegundos();
            cronometro.setText(tiempoTexto);
            
            if (modeloCronometro.esTiempoMaximo()) {
                detenerSistemas();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        TextField[][] tablero = {
            {a1, b1, c1, d1}, {a2, b2, c2, d2},
            {a3, b3, c3, d3}, {a4, b4, c4, d4},
            {a5, b5, c5, d5}, {a6, b6, c6, d6}
        };
        Label[] resultados = {res1, res2, res3, res4, res5, res6};

        // Agregación: se instancia Juego pasándole el Usuario desde la sesión
        Usuario usuario = SesionUsuario.getInstancia().getUsuarioActual();
        juego = new Juego(true, usuario, tablero, resultados);
        juego.GenerarNuevoJuego();
        juego.BloquearTodo();
        juego.HabilitarFila(0);
    }
    private String detenerSistemas() {
        if (timeline != null) timeline.stop();
        return "Cronómetro detenido";
    }
    // --- Controles numéricos ---
    @FXML void Click1(ActionEvent e)  { juego.EscribirNumero("1");  }
    @FXML void Click2(ActionEvent e)  { juego.EscribirNumero("2");  }
    @FXML void Click3(ActionEvent e)  { juego.EscribirNumero("3");  }
    @FXML void Click4(ActionEvent e)  { juego.EscribirNumero("4");  }
    @FXML void Click5(ActionEvent e)  { juego.EscribirNumero("5");  }
    @FXML void Click6(ActionEvent e)  { juego.EscribirNumero("6");  }
    @FXML void Click7(ActionEvent e)  { juego.EscribirNumero("7");  }
    @FXML void Click8(ActionEvent e)  { juego.EscribirNumero("8");  }
    @FXML void Click9(ActionEvent e)  { juego.EscribirNumero("9");  }
    @FXML void Click10(ActionEvent e) { juego.EscribirNumero("10"); }
    @FXML void Click11(ActionEvent e) { juego.EscribirNumero("11"); }
    @FXML void Click12(ActionEvent e) { juego.EscribirNumero("12"); }

    @FXML void ClickDel(ActionEvent e) { juego.BorrarDigito(); }
    
    @FXML void ClickRestart(ActionEvent e) {
        detenerSistemas();
        cronometro.setText(modeloCronometro.reiniciar());
        timeline.playFromStart();
        juego.ReiniciarJuego();
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        String resultadoValidacion = juego.ValidarFila();
        if (resultadoValidacion == null) return;

        // Obtenemos el usuario desde el propio objeto juego (via agregación)
        Usuario usuario = juego.getUsuario();
        int id = usuario.getId();

        if (resultadoValidacion.equals("GANASTE")) {
            ResultadoPartida datos = new ResultadoPartida(id, 1, 1, 1);
            Usuario actualizado = daoEstadisticas.actualizarDatos(datos);
            SesionUsuario.getInstancia().setUsuarioActual(actualizado);
            cambiarEscena(e, "VictoriaDiario.fxml");
            
        } else if (resultadoValidacion.equals("PERDISTE")) {
            detenerSistemas();
            ResultadoPartida datos = new ResultadoPartida(id, -1, 0, 1);
            Usuario actualizado = daoEstadisticas.actualizarDatos(datos);
            SesionUsuario.getInstancia().setUsuarioActual(actualizado);
            cambiarEscena(e, "DerrotaDiario.fxml");
        }
    }

    private void cambiarEscena(ActionEvent evento, String fxml) {
        try {
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/" + fxml));
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
            detenerSistemas();
            Parent root = FXMLLoader.load(getClass().getResource("/ftgw/ooodle/interfaces/Lobby.fxml"));
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
