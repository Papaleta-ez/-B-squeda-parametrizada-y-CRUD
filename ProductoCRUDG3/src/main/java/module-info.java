module com.example.productocrudg3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.productocrudg3 to javafx.fxml;
    exports com.example.productocrudg3;
}