package ProyectoCrud.Aplication;

import ProyectoCrud.conection.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class Aplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Aplication.class.getResource("/com/example/demo/producto-view.fxml")
        );
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Gestión de productos");
        stage.show();

        try (Connection conexion = DatabaseConnection.getConnection()) {
            stage.setTitle("Gestión de productos - bdventas conectada");
        } catch (SQLException ex) {
            Alert aviso = new Alert(Alert.AlertType.ERROR);
            aviso.initOwner(stage);
            aviso.setTitle("Conexión a bdventas");
            aviso.setHeaderText("No se pudo conectar a la base de datos");
            aviso.setContentText(ex.getMessage());
            aviso.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
