module ftgw.ooodle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;


    opens ftgw.ooodle to javafx.fxml;
    opens ftgw.ooodle.Controladores to javafx.fxml;
    opens Servicios to javafx.fxml;

    exports ftgw.ooodle;
    exports Servicios;
}