package ftgw.ooodle.Modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class RelojDiario {

    private final Label labelReloj;
    private final Button btnDiario;
    private final String nombreUsuario; // Necesitamos el nombre para la BD
    private Timeline timeline;

    public RelojDiario(Label labelReloj, Button btnDiario, String nombreUsuario) {
        this.labelReloj = labelReloj;
        this.btnDiario = btnDiario;
        this.nombreUsuario = nombreUsuario;
        
    }

    public void iniciar() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
            long segundosRestantes = ChronoUnit.SECONDS.between(ahora, medianoche);

            if (segundosRestantes <= 0) {
                Estadisticas stats = usuarioDAO.obtenerEstadisticas(nombreUsuario);

            
                if (!stats.diarioJugadoHoy) {
                    stats.rachaActual = 0; // Se rompe la racha si no jugó
                }
                stats.diarioJugadoHoy = false;
                stats.ultimoDiaJugado = ahora.toLocalDate().toString();


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