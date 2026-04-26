package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.time.LocalDate;

import Servicios.Estadisticas;
import Servicios.UsuarioDAO; // Importamos el nuevo DAO
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CJuegoDiarioFacil {

    @FXML private Button B1, B2, B3, B4, B5, B6, B7, B8, B9;
    @FXML private Button Bcheck, Bdel, Blobby, Brestart;
    @FXML private AnchorPane PanelBase;

    @FXML private TextField a1, a2, a3, a4, a5, a6;
    @FXML private TextField b1, b2, b3, b4, b5, b6;
    @FXML private TextField c1, c2, c3, c4, c5, c6;
    @FXML private TextField d1, d2, d3, d4, d5, d6;

    @FXML private Label res1, res2, res3, res4, res5, res6;
    @FXML private Label cronometro;

    private Juego juego;
    private CronometroJuego cronometroJuego;
    
    // NUEVO: Manejo de base de datos
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private String nombreUsuarioActual = "Jugador1"; // Temporal, igual que en el Lobby

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

        juego = new Juego(false, tablero, resultados);
        juego.GenerarNuevoJuego();
        juego.BloquearTodo();
        juego.HabilitarFila(0);
    }

    // ... Métodos Click1 al Click9 se mantienen igual ...
    @FXML void Click1(ActionEvent e) { juego.EscribirNumero("1"); }
    @FXML void Click2(ActionEvent e) { juego.EscribirNumero("2"); }
    @FXML void Click3(ActionEvent e) { juego.EscribirNumero("3"); }
    @FXML void Click4(ActionEvent e) { juego.EscribirNumero("4"); }
    @FXML void Click5(ActionEvent e) { juego.EscribirNumero("5"); }
    @FXML void Click6(ActionEvent e) { juego.EscribirNumero("6"); }
    @FXML void Click7(ActionEvent e) { juego.EscribirNumero("7"); }
    @FXML void Click8(ActionEvent e) { juego.EscribirNumero("8"); }
    @FXML void Click9(ActionEvent e) { juego.EscribirNumero("9"); }

    @FXML void ClickDel(ActionEvent e)     { juego.BorrarDigito(); }
    @FXML void ClickRestart(ActionEvent e) {
        cronometroJuego.ReiniciarCronometro();
        juego.ReiniciarJuego();
    }

    @FXML
    void ClickCheck(ActionEvent e) {
        String resultado = juego.ValidarFila();
        if (resultado == null) return;

        // Cargamos las estadísticas actuales de la BD
        Estadisticas stats = usuarioDAO.obtenerEstadisticas(nombreUsuarioActual);

        switch (resultado) {
            case "GANASTE":
                // Aplicamos la lógica que antes estaba en el Service
                stats.partidasJugadas++;
                stats.partidasGanadas++;
                stats.rachaActual++;
                if (stats.rachaActual > stats.rachaMaxima) stats.rachaMaxima = stats.rachaActual;
                
                int idx = Math.max(0, Math.min(5, juego.GetIntentoActual() - 1));
                stats.indiceAdivinanza[idx]++;
                stats.diarioJugadoHoy = true;
                stats.ultimoDiaJugado = LocalDate.now().toString();

                // Guardamos en MySQL
                usuarioDAO.actualizarEstadisticas(nombreUsuarioActual, stats);
                cambiarEscena(e, "VictoriaDiario.fxml");
                break;

            case "PERDISTE":
                stats.partidasJugadas++;
                stats.rachaActual = 0;
                stats.diarioJugadoHoy = true;
                stats.ultimoDiaJugado = LocalDate.now().toString();

                // Guardamos en MySQL
                usuarioDAO.actualizarEstadisticas(nombreUsuarioActual, stats);
                cambiarEscena(e, "DerrotaDiario.fxml");
                break;
        }
    }

    // ... cambiarEscena y volverAlLobby se mantienen igual ...
    private void cambiarEscena(ActionEvent evento, String fxml) {
        try {
            cronometroJuego.DetenerCronometro();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/" + fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, ((Node) evento.getSource()).getScene().getWidth(), ((Node) evento.getSource()).getScene().getHeight()));
            stage.setMaximized(true);
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
            stage.setScene(new Scene(root, ((Node) e.getSource()).getScene().getWidth(), ((Node) e.getSource()).getScene().getHeight()));
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}