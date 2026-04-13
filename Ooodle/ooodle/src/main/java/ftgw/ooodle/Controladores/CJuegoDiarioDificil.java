package ftgw.ooodle.Controladores;

import java.io.IOException;
import Servicios.EstadisticasService;
import ftgw.ooodle.Modelo.CronometroJuego;
import ftgw.ooodle.Modelo.Juego;
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

public class CJuegoDiarioDificil {

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12;
    @FXML private Button Bcheck, Bdel, Blooby, Brestart;

    @FXML private TextField a1, a2, a3, a4, a5, a6;
    @FXML private TextField b1, b2, b3, b4, b5, b6;
    @FXML private TextField c1, c2, c3, c4, c5, c6;
    @FXML private TextField d1, d2, d3, d4, d5, d6;

    @FXML private Label res1, res2, res3, res4, res5, res6;
    @FXML private Label cronometro;

    private Juego juego;
    private CronometroJuego cronometroJuego;

    @FXML
    public void initialize() {
        cronometroJuego = new CronometroJuego(cronometro);
        cronometroJuego.initialize();

        TextField[][] tablero = {
            {a1, b1, c1, d1}, {a2, b2, c2, d2},
            {a3, b3, c3, d3}, {a4, b4, c4, d4},
            {a5, b5, c5, d5}, {a6, b6, c6, d6}
        };
        Label[] resultados = {res1, res2, res3, res4, res5, res6};

        juego = new Juego(true, tablero, resultados);
        juego.GenerarNuevoJuego();
        juego.BloquearTodo();
        juego.HabilitarFila(0);
    }

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

    @FXML void ClickDel(ActionEvent e)     { juego.BorrarDigito(); }
    @FXML void ClickRestart(ActionEvent e) {
        cronometroJuego.ReiniciarCronometro();
        juego.ReiniciarJuego();
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        String resultado = juego.ValidarFila();
        if (resultado == null) return;

        // FIX: break en cada case para evitar fall-through.
        // FIX: marcarDiarioJugado() eliminado — registrarVictoria/Derrota ya lo hacen internamente.
        switch (resultado) {
            case "GANASTE":
                EstadisticasService.registrarVictoria(juego.GetIntentoActual());
                cambiarEscena(e, "VictoriaDiario.fxml");
                break;
            case "PERDISTE":
                EstadisticasService.registrarDerrota();
                cambiarEscena(e, "DerrotaDiario.fxml");
                break;
        }
    }

    private void cambiarEscena(ActionEvent evento, String fxml) {
        try {
            cronometroJuego.DetenerCronometro();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void volverAlLobby(ActionEvent e) {
        try {
            cronometroJuego.DetenerCronometro();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}