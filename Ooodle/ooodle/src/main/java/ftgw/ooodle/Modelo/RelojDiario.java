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
    private final boolean puedeJugarInicial; // Nueva variable
    private Timeline timeline;

    // Actualizamos el constructor
    public RelojDiario(Label labelReloj, Button btnDiario, boolean puedeJugarInicial) {
        this.labelReloj = labelReloj;
        this.btnDiario = btnDiario;
        this.puedeJugarInicial = puedeJugarInicial;
    }

    public void iniciar() {
        // Ajuste inicial: si la BD dice que puede jugar, lo habilitamos de inmediato
        if (puedeJugarInicial) {
            btnDiario.setDisable(false);
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime medianoche = ahora.toLocalDate().plusDays(1).atStartOfDay();
            long segundosRestantes = ChronoUnit.SECONDS.between(ahora, medianoche);

            if (segundosRestantes <= 0) {
                btnDiario.setDisable(false);
                labelReloj.setText("00:00:00");
            } else {
                // SOLO si el usuario NO puede jugar (ya gastó su partida), 
                // el reloj mantiene el botón bloqueado.
                if (!puedeJugarInicial) {
                    btnDiario.setDisable(true);
                }

                long horas = segundosRestantes / 3600;
                long minutos = (segundosRestantes % 3600) / 60;
                long segundos = segundosRestantes % 60;
                labelReloj.setText(String.format("%02d:%02d:%02d", horas, minutos, segundos));
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    public void detener() {
        if (timeline != null) timeline.stop();
    } 
}