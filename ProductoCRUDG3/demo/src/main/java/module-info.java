module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens com.example.demo to javafx.fxml;
    opens ProyectoCrud.Controller to javafx.fxml;
    exports com.example.demo;
    exports ProyectoCrud.Aplication;
}
