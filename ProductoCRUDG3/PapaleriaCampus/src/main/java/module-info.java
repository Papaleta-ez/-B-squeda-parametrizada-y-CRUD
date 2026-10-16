module ni.edu.uam.papaleriacampus {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.papaleriacampus to javafx.fxml;
    exports ni.edu.uam.papaleriacampus;
}