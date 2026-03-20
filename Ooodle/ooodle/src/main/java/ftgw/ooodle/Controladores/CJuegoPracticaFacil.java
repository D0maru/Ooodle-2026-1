package ftgw.ooodle.Controladores;

import java.io.IOException;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class CJuegoPracticaFacil {

    @FXML
    private Button BCheck;
    @FXML
    private Button BDel;
    @FXML
    private Button BT1;
    @FXML
    private Button BT2;
    @FXML
    private Button BT3;
    @FXML
    private Button BT4;
    @FXML
    private Button BT5;
    @FXML
    private Button BT6;
    @FXML
    private Button BT7;
    @FXML
    private Button BT8;
    @FXML
    private Button BT9;
    @FXML
    private Button BTRestart;
    @FXML
    void Click(ActionEvent event) {
         // Esto sirve para saber qué botón tocaste en la consola
        Button botonPresionado = (Button) event.getSource();
        if(botonPresionado == BTRestart){
            reiniciarCronometro();
        }
        System.out.println("Presionaste el botón: " + botonPresionado.getText());
    }
    @FXML
    private Button BtnRPP; 
    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            // 1. Cargar el archivo FXML del lobby
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml"));
            Parent root = loader.load();
            // 2. Obtener el Stage (la ventana) actual a partir del botón que se presionó
            Stage stage = (Stage) BtnRPP.getScene().getWindow();
            // 3. Cambiar la escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar el lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private Label cronometro;
    private int segundosTranscurridos = 0;
    private Timeline timeline;
    
    @FXML
    public void initialize(){
        //Iniciarmos el cronometro
        cronometro.setText("00:00");
        //Logica del cronometro 
        timeline = new Timeline(new KeyFrame(Duration.seconds(1),event ->{ 
            segundosTranscurridos++;
            actualizarLabel();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    private void actualizarLabel(){
        int minutos = segundosTranscurridos / 60;
        int segundos = segundosTranscurridos % 60;
        String tiempo = String.format("%02d:%02d", minutos, segundos);
        cronometro.setText(tiempo);
    }
    private void reiniciarCronometro(){
        timeline.stop();
        segundosTranscurridos = 0;
        actualizarLabel();
        timeline.play();
    }

}
