module ftgw.ooodle {
    // Módulos de JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Librerías externas
    requires com.google.gson;
    requires io.github.cdimascio.dotenv.java; 
    requires java.sql; 
    

    // Abrir paquetes para que JavaFX pueda leer los FXML y GSON pueda serializar
    opens ftgw.ooodle to javafx.fxml;
    opens ftgw.ooodle.Controladores to javafx.fxml;
    
    // Si tus clases de servicio usan GSON para convertir objetos a JSON, 
    // necesitan estar abiertas al módulo de GSON
    opens Servicios to com.google.gson, javafx.fxml;

    // Exportar paquetes para que sean visibles
    exports ftgw.ooodle;
    exports Servicios;
}