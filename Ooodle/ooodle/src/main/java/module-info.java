module ftgw.ooodle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ftgw.ooodle to javafx.fxml;
    opens ftgw.ooodle.Controladores to javafx.fxml;
    

    exports ftgw.ooodle;
}
