module ftgw.ooodle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires io.github.cdimascio.dotenv.java; 
    requires java.sql;        


    opens ftgw.ooodle to javafx.fxml;
    opens ftgw.ooodle.Controladores to javafx.fxml;
    
    opens Servicios to com.google.gson, javafx.fxml;

    exports ftgw.ooodle;
    exports Servicios;
}