package ftgw.ooodle.Modelo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import Servicios.Estadisticas;
import Servicios.EstadisticasService;

public class RelojDiario {

    private final Label labelReloj;
    private final Button btnDiario;
    private Timeline timeline;

    public RelojDiario(Label labelReloj, Button btnDiario) {
        this.labelReloj = labelReloj;
        this.btnDiario = btnDiario;
    }

    public void iniciar() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
            long segundosRestantes = ChronoUnit.SECONDS.between(ahora, medianoche);

            if (segundosRestantes <= 0) {
                segundosRestantes = ChronoUnit.SECONDS.between(
                    LocalDateTime.now(),
                    LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
                );

                Estadisticas stats = EstadisticasService.cargar();
                if (!stats.diarioJugadoHoy) {
                    stats.rachaActual = 0;
                }
                stats.diarioJugadoHoy = false;
                stats.ultimoDiaJugado = ahora.toLocalDate().plusDays(1).toString();
                EstadisticasService.guardar(stats);
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