package ftgw.ooodle;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class ContollerI {

    // Este método recibirá los clics de todos tus botones
    @FXML
    private void Click(ActionEvent event) {
        // Esto sirve para saber qué botón tocaste en la consola
        Button botonPresionado = (Button) event.getSource();
        System.out.println("Presionaste el botón: " + botonPresionado.getText());
    }
}
