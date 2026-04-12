package ftgw.ooodle.Modelo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CronometroJuego {

    private int segundosTranscurridos = 0;
    public Timeline timeline;
    private Label cronometro;

    public CronometroJuego(Label cronometro) {
        this.cronometro = cronometro;
    }

    public void initialize() {
        cronometro.setText("00:00");
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            segundosTranscurridos++;
            ActualizarCronometro();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void DetenerCronometro() {
        if (timeline != null) timeline.stop();
    }

    public void ReiniciarCronometro() {
        segundosTranscurridos = 0;
        ActualizarCronometro();
        if (timeline != null) timeline.playFromStart();
    }

    private void ActualizarCronometro() {
        if (segundosTranscurridos >= 3600) {
            cronometro.setText("¡Te demoraste mucho!");
            timeline.stop();
            return;
        }
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        cronometro.setText(String.format("%02d:%02d", minutos, segundos));
    }
}