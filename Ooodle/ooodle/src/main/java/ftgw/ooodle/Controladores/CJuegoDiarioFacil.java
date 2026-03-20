package ftgw.ooodle.Controladores;

import java.io.IOException;
import java.net.URL;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; // Importante para obtener el Stage
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button; // Faltaba este import
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CJuegoDiarioFacil {
    @FXML
    private AnchorPane PanelBase;
    @FXML
    private Label letrero;
    @FXML
    private Button BtnRPP;
     @FXML
    private Button BtnRestart;
    //El siguiente metodo hace que cuando se presione el boton restart se reinicie el cronometro 
    @FXML
    void Click(ActionEvent event) {
         // Esto sirve para saber qué botón tocaste en la consola
        Button botonPresionado = (Button) event.getSource();
        if(botonPresionado == BtnRestart){
            reiniciarCronometro();
        }
        System.out.println("Presionaste el botón: " + botonPresionado.getText());
    }
    @FXML
    void volverAlLobby(ActionEvent event) {
        try {
            URL fxmlLocation = getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml");
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("/ftgw/ooodle/Vista/Lobby.fxml");
            }
            if (fxmlLocation == null) {
                throw new IOException("No se pudo encontrar lobby.fxml. Revisa la ubicación del archivo.");
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cambiar de pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private Label cronometro;
    private int segundosTranscurridos = 0;
    private Timeline timeline;
    //Los siguientes metodos sirver para crrear, iniciar,actualizar y reiniciar el cronometro
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
