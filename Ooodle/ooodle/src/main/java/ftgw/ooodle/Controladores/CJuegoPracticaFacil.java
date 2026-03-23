package ftgw.ooodle.Controladores;

import java.io.IOException;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private Button BtnRPP;

    @FXML
    private TextField a1;

    @FXML
    private TextField a2;

    @FXML
    private TextField a3;

    @FXML
    private TextField a4;

    @FXML
    private TextField a5;

    @FXML
    private TextField a6;

    @FXML
    private TextField b1;

    @FXML
    private TextField b2;

    @FXML
    private TextField b3;

    @FXML
    private TextField b4;

    @FXML
    private TextField b5;

    @FXML
    private TextField b6;

    @FXML
    private TextField c1;

    @FXML
    private TextField c2;

    @FXML
    private TextField c3;

    @FXML
    private TextField c4;

    @FXML
    private TextField c5;

    @FXML
    private TextField c6;

    @FXML
    private Label cronometro;

    @FXML
    private TextField d1;

    @FXML
    private TextField d2;

    @FXML
    private TextField d3;

    @FXML
    private TextField d4;

    @FXML
    private TextField d5;

    @FXML
    private TextField d6;

    @FXML
    private Label res_1;

    @FXML
    private Label res_2;

    @FXML
    private Label res_3;

    @FXML
    private Label res_4;

    @FXML
    private Label res_5;

    @FXML
    private Label res_6;
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
    void volverAlLobby(ActionEvent event) {
        try {
            // 1. Cargar el archivo FXML del lobby
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
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
        segundosTranscurridos = 0;
        actualizarLabel();
        timeline.playFromStart();
    }

}
