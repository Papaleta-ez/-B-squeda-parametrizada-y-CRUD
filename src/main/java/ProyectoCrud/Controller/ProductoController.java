package ProyectoCrud.Controller;

import ProyectoCrud.conection.DatabaseConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProductoController {
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();
    private final ObservableList<Marca> listaMarcas = FXCollections.observableArrayList();

    private record Producto(
            int idProducto,
            String codigo,
            String nombre,
            String descripcion,
            double precio,
            int stock,
            LocalDate fechaCreacion,
            int idCategoria,
            String categoria,
            int idMarca,
            String marca
    ) {}

    private record Categoria(int idCategoria, String nombre) {
        @Override
        public String toString() { return nombre; }
    }

    private record Marca(int idMarca, String nombre) {
        @Override
        public String toString() { return nombre; }
    }

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private ComboBox<Marca> cmbMarca;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private TextField txtPrecioMin;

    @FXML
    private TextField txtPrecioMax;

    @FXML
    private CheckBox chkConExistencias;

    @FXML
    private ComboBox<String> cmbOrdenar;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFecha;

    @FXML
    private void initialize() {
        configurarTableView();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarMarcas();
        cargarOpcionesOrden();
        cargarProductos();
    }

    private void configurarTableView() {
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().codigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().categoria()));
        colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().marca()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().precio()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().stock()).asObject());
        colFecha.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().fechaCreacion()));
        tablaProductos.setItems(listaProductos);
    }

    private void configurarSeleccionTableView() {
        tablaProductos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        cargarProductoEnFormulario(seleccionado);
                    }
                }
        );
    }

    private void cargarProductoEnFormulario(Producto producto) {
        txtCodigo.setText(producto.codigo());
        txtNombre.setText(producto.nombre());
        txtDescripcion.setText(producto.descripcion());
        txtPrecio.setText(String.valueOf(producto.precio()));
        txtStock.setText(String.valueOf(producto.stock()));
        dpFecha.setValue(producto.fechaCreacion());

        for (Categoria categoria : listaCategorias) {
            if (categoria.idCategoria() == producto.idCategoria()) {
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        for (Marca marca : listaMarcas) {
            if (marca.idMarca() == producto.idMarca()) {
                cmbMarca.setValue(marca);
                break;
            }
        }
    }

    private void cargarCategorias() {
        listaCategorias.clear();
        String sql = "SELECT id_categoria, nombre FROM catalogos.categoria WHERE estado = 1 ORDER BY nombre";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet filas = consulta.executeQuery()) {
            while (filas.next()) {
                listaCategorias.add(new Categoria(filas.getInt("id_categoria"), filas.getString("nombre")));
            }
            cmbCategoria.setItems(listaCategorias);
            cmbFiltroCategoria.setItems(listaCategorias);
        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar las categorías.", e);
        }
    }

    private void cargarMarcas() {
        listaMarcas.clear();
        String sql = "SELECT id_marca, nombre FROM catalogos.marcas WHERE estado = 1 ORDER BY nombre";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet filas = consulta.executeQuery()) {
            while (filas.next()) {
                listaMarcas.add(new Marca(filas.getInt("id_marca"), filas.getString("nombre")));
            }
            cmbMarca.setItems(listaMarcas);
        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar las marcas.", e);
        }
    }

    private void cargarOpcionesOrden() {
        cmbOrdenar.setItems(FXCollections.observableArrayList(
                "Nombre", "Precio menor a mayor", "Precio mayor a menor",
                "Fecha más reciente", "Fecha más antigua",
                "Stock mayor a menor", "Stock menor a mayor"
        ));
        cmbOrdenar.setValue("Nombre");
    }

    private void cargarProductos() {
        listaProductos.clear();
        String sql = """
                SELECT p.id_producto, p.codigo, p.nombre, p.descripcion,
                       p.precio, p.stock, p.fecha_creacion, p.id_categoria,
                       c.nombre AS categoria, p.id_marca, m.nombre AS marca
                FROM catalogos.productos AS p
                INNER JOIN catalogos.categoria AS c ON p.id_categoria = c.id_categoria
                INNER JOIN catalogos.marcas AS m ON p.id_marca = m.id_marca
                WHERE p.estado = 1
                ORDER BY p.nombre
                """;

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet filas = consulta.executeQuery()) {
            while (filas.next()) {
                listaProductos.add(crearProductoDesdeResultSet(filas));
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos("No se pudieron cargar los productos.", e);
        }
    }

    private Producto crearProductoDesdeResultSet(ResultSet filas) throws SQLException {
        return new Producto(
                filas.getInt("id_producto"),
                filas.getString("codigo"),
                filas.getString("nombre"),
                filas.getString("descripcion"),
                filas.getDouble("precio"),
                filas.getInt("stock"),
                filas.getDate("fecha_creacion").toLocalDate(),
                filas.getInt("id_categoria"),
                filas.getString("categoria"),
                filas.getInt("id_marca"),
                filas.getString("marca")
        );
    }

    private void mostrarErrorBaseDatos(String mensaje, SQLException e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de base de datos");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
    }

    @FXML
    private void Nuevo_producto(ActionEvent event) {
    }

    @FXML
    private void guardar_producto(ActionEvent event) {
    }

    @FXML
    private void Actualizar_producto(ActionEvent event) {
    }

    @FXML
    private void Eliminar_producto(ActionEvent event) {
    }

    @FXML
    private void Buscar_producto(ActionEvent event) {
    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {
    }
}
