package ftgw.ooodle.Modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import Servicios.Estadisticas;
import Servicios.UsuarioDAO; // Importamos el nuevo DAO

public class RelojDiario {

    private final Label labelReloj;
    private final Button btnDiario;
    private final String nombreUsuario; // Necesitamos el nombre para la BD
    private final UsuarioDAO usuarioDAO; // Instancia del DAO
    private Timeline timeline;

    public RelojDiario(Label labelReloj, Button btnDiario, String nombreUsuario) {
        this.labelReloj = labelReloj;
        this.btnDiario = btnDiario;
        this.nombreUsuario = nombreUsuario;
        this.usuarioDAO = new UsuarioDAO();
    }

    public void iniciar() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
            long segundosRestantes = ChronoUnit.SECONDS.between(ahora, medianoche);

            if (segundosRestantes <= 0) {
                // LÓGICA DE REINICIO A MEDIANOCHE
                // 1. Obtenemos las estadísticas actuales del usuario desde la BD
                Estadisticas stats = usuarioDAO.obtenerEstadisticas(nombreUsuario);

                // 2. Aplicamos la lógica de cambio de día
                if (!stats.diarioJugadoHoy) {
                    stats.rachaActual = 0; // Se rompe la racha si no jugó
                }
                stats.diarioJugadoHoy = false;
                stats.ultimoDiaJugado = ahora.toLocalDate().toString();

                // 3. Guardamos el JSON actualizado en MySQL
                usuarioDAO.actualizarEstadisticas(nombreUsuario, stats);

                btnDiario.setDisable(false);
            }

            long horas   = segundosRestantes / 3600;
            long minutos = (segundosRestantes % 3600) / 60;
            long segundos = segundosRestantes % 60;

            labelReloj.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void detener() {
        if (timeline != null) timeline.stop();
    }
}