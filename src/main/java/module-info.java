module org.example.program3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.program3 to javafx.fxml;
    exports org.example.program3;
}