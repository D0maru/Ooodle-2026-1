module ftgw.ooodle {
    requires javafx.controls;
    requires javafx.fxml;

    opens ftgw.ooodle to javafx.fxml;
    opens ftgw.ooodle.Controladores to javafx.fxml;
    

    exports ftgw.ooodle;
}
