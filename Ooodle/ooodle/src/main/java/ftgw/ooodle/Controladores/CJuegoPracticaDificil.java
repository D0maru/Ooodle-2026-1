package ftgw.ooodle.Controladores;

import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CJuegoPracticaDificil {
    //---- Metodos cronometro ----
    @FXML
    private Label cronometro;
    private int segundosTranscurridos =0;
    private Timeline timeline;
    @FXML
    public void initialize(){

        // ===== CRONÓMETRO =====
        cronometro.setText("00:00");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1),event ->{ 
            segundosTranscurridos++;
            actualizarLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    private void actualizarLabel(){
        if(segundosTranscurridos >= 3600){
            cronometro.setText("!Te demoraste mucho!");
            timeline.stop();
            return;
        }
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        String tiempo = String.format("%02d:%02d", minutos, segundos);
        cronometro.setText(tiempo);
        
    }

    private void reiniciarCronometro(){
        segundosTranscurridos = 0;
        actualizarLabel();
        timeline.playFromStart();
    }
    @FXML
    private Button BtnVL;
    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BtnVL.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void ClickRestart(ActionEvent event){
        reiniciarCronometro();
    }
}
