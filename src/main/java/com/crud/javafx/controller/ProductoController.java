package com.crud.javafx.controller;

import com.crud.javafx.model.Producto;
import com.crud.javafx.service.IProductoService;
import com.crud.javafx.service.ProductoService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador de la vista principal del CRUD de Productos.
 * Gestiona la lógica de presentación, validación de entradas de usuario,
 * enlace de datos (data-binding) con la TableView y comunicación con el Servicio.
 */
public class ProductoController implements Initializable {

    // ==========================================
    // COMPONENTES FXML INYECTADOS
    // ==========================================

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtCantidad;

    @FXML
    private Label lblErrorNombre;

    @FXML
    private Label lblErrorPrecio;

    @FXML
    private Label lblErrorCantidad;

    @FXML
    private Label lblModoFormulario;

    @FXML
    private HBox bannerNotificacion;

    @FXML
    private Label lblMensajeEstado;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnLimpiarBusqueda;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Label lblTotalProductos;

    @FXML
    private Label lblStockTotal;

    @FXML
    private Label lblValorInventario;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, Integer> colId;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colCantidad;

    @FXML
    private TableColumn<Producto, Double> colSubtotal;

    @FXML
    private Label lblEstadoPie;

    // ==========================================
    // ATRIBUTOS DE NEGOCIO Y ESTADO
    // ==========================================

    private final IProductoService productoService = new ProductoService();
    private final ObservableList<Producto> listaObservable = FXCollections.observableArrayList();
    private FilteredList<Producto> listaFiltrada;

    private boolean modoEdicion = false;
    private Producto productoSeleccionadoParaEdicion = null;

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Enumerador para tipificar visualmente las notificaciones del banner.
     */
    private enum TipoNotificacion {
        EXITO, ERROR, INFO
    }

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnasTabla();
        configurarSeleccionYEventosTabla();
        configurarBuscadorEnTiempoReal();
        configurarValidacionEnTiempoReal();
        cargarDatosDesdeServicio();
        actualizarMetricasDashboard();
        restablecerFormularioAModoNuevo();
        mostrarNotificacion(TipoNotificacion.INFO, "Aplicación iniciada. Datos cargados en memoria (ArrayList).");
    }

    /**
     * Define las PropertyValueFactory y formatos visuales para cada columna de la TableView.
     */
    private void configurarColumnasTabla() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        colCantidad.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        colSubtotal.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSubtotal()).asObject());

        // Formato para columna Precio ($ 0.00)
        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(precio));
                }
            }
        });

        // Formato para columna Subtotal ($ 0.00)
        colSubtotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(formatoMoneda.format(subtotal));
                }
            }
        });

        // Formato para columna Cantidad (centrado)
        colCantidad.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                if (empty || cantidad == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(cantidad));
                }
            }
        });
    }

    /**
     * Vincula la selección de filas con la activación de botones de acción
     * y habilita la edición rápida con doble clic.
     */
    private void configurarSeleccionYEventosTabla() {
        // Habilitar / deshabilitar botones Editar y Eliminar según la selección
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            boolean haySeleccion = seleccionado != null;
            btnEditar.setDisable(!haySeleccion);
            btnEliminar.setDisable(!haySeleccion);
        });

        // Doble clic en una fila para cargarla directamente en el formulario de edición
        tablaProductos.setRowFactory(tv -> {
            TableRow<Producto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    cargarProductoEnFormulario(row.getItem());
                }
            });
            return row;
        });
    }

    /**
     * Configura el filtrado en tiempo real sin recargar la pantalla.
     */
    private void configurarBuscadorEnTiempoReal() {
        listaFiltrada = new FilteredList<>(listaObservable, p -> true);

        txtBuscar.textProperty().addListener((observable, oldValue, nuevoTexto) -> {
            listaFiltrada.setPredicate(producto -> {
                if (nuevoTexto == null || nuevoTexto.trim().isEmpty()) {
                    return true;
                }

                String criterio = nuevoTexto.trim().toLowerCase();
                boolean coincideId = String.valueOf(producto.getId()).contains(criterio);
                boolean coincideNombre = producto.getNombre().toLowerCase().contains(criterio);

                return coincideId || coincideNombre;
            });
            actualizarMetricasDashboard();
        });

        SortedList<Producto> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(listaOrdenada);
    }

    /**
     * Limpia los estilos de error automáticamente cuando el usuario comienza a escribir.
     */
    private void configurarValidacionEnTiempoReal() {
        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                limpiarErrorCampo(txtNombre, lblErrorNombre);
            }
        });

        txtPrecio.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                limpiarErrorCampo(txtPrecio, lblErrorPrecio);
            }
        });

        txtCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                limpiarErrorCampo(txtCantidad, lblErrorCantidad);
            }
        });
    }

    /**
     * Carga los elementos iniciales del servicio en memoria hacia la ObservableList.
     */
    private void cargarDatosDesdeServicio() {
        listaObservable.setAll(productoService.listarTodos());
    }

    /**
     * Recalcula las tarjetas de métricas superiores (KPIs de inventario).
     */
    private void actualizarMetricasDashboard() {
        int total = listaObservable.size();
        int stock = listaObservable.stream().mapToInt(Producto::getCantidad).sum();
        double valor = listaObservable.stream().mapToDouble(Producto::getSubtotal).sum();

        lblTotalProductos.setText(String.valueOf(total));
        lblStockTotal.setText(String.valueOf(stock));
        lblValorInventario.setText(formatoMoneda.format(valor));
    }

    // ==========================================
    // OPERACIONES CRUD
    // ==========================================

    /**
     * Acción del botón Guardar (funciona tanto para creación como para edición).
     */
    @FXML
    public void guardarProducto(ActionEvent event) {
        if (!validarFormulario()) {
            mostrarNotificacion(TipoNotificacion.ERROR, "Por favor corrija los campos marcados en rojo antes de continuar.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());

        if (modoEdicion && productoSeleccionadoParaEdicion != null) {
            // =========================
            // OPERACIÓN: ACTUALIZAR (U)
            // =========================
            productoSeleccionadoParaEdicion.setNombre(nombre);
            productoSeleccionadoParaEdicion.setPrecio(precio);
            productoSeleccionadoParaEdicion.setCantidad(cantidad);

            boolean actualizado = productoService.actualizar(productoSeleccionadoParaEdicion);
            if (actualizado) {
                // Forzar refresco visual en la tabla
                tablaProductos.refresh();
                actualizarMetricasDashboard();
                mostrarNotificacion(TipoNotificacion.EXITO, "Producto ID #" + productoSeleccionadoParaEdicion.getId() + " actualizado exitosamente.");
                restablecerFormularioAModoNuevo();
            } else {
                mostrarNotificacion(TipoNotificacion.ERROR, "No se pudo actualizar el producto en el ArrayList.");
            }

        } else {
            // =========================
            // OPERACIÓN: CREAR (C)
            // =========================
            Producto nuevo = new Producto(nombre, precio, cantidad);
            Producto creado = productoService.crear(nuevo);

            listaObservable.add(creado);
            actualizarMetricasDashboard();
            tablaProductos.getSelectionModel().select(creado);
            tablaProductos.scrollTo(creado);

            mostrarNotificacion(TipoNotificacion.EXITO, "Producto '" + creado.getNombre() + "' registrado con éxito (ID #" + creado.getId() + ").");
            limpiarCamposFormulario();
        }
    }

    /**
     * Acción para preparar el formulario para un nuevo registro.
     */
    @FXML
    public void nuevoProducto(ActionEvent event) {
        restablecerFormularioAModoNuevo();
        mostrarNotificacion(TipoNotificacion.INFO, "Formulario listo para ingresar un nuevo producto.");
    }

    /**
     * Acción para cargar el registro seleccionado en el formulario para editarlo.
     */
    @FXML
    public void editarProducto(ActionEvent event) {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarNotificacion(TipoNotificacion.ERROR, "Seleccione un producto de la tabla para editar.");
            return;
        }
        cargarProductoEnFormulario(seleccionado);
    }

    /**
     * Acción para eliminar el producto seleccionado con confirmación previa.
     */
    @FXML
    public void eliminarProducto(ActionEvent event) {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarNotificacion(TipoNotificacion.ERROR, "Seleccione un producto de la tabla para eliminar.");
            return;
        }

        // ==========================================
        // DIÁLOGO DE CONFIRMACIÓN UX
        // ==========================================
        Alert alertConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirmacion.setTitle("Confirmar Eliminación");
        alertConfirmacion.setHeaderText("¿Está seguro de eliminar el siguiente producto?");
        alertConfirmacion.setContentText(
                "ID: " + seleccionado.getId() + "\n" +
                "Nombre: " + seleccionado.getNombre() + "\n" +
                "Precio: " + formatoMoneda.format(seleccionado.getPrecio()) + "\n" +
                "Stock: " + seleccionado.getCantidad() + " unidades\n\n" +
                "Esta acción eliminará el registro del ArrayList en memoria."
        );

        ButtonType btnAceptar = new ButtonType("Sí, Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelarDialogo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertConfirmacion.getButtonTypes().setAll(btnAceptar, btnCancelarDialogo);

        Optional<ButtonType> resultado = alertConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnAceptar) {
            // =========================
            // OPERACIÓN: ELIMINAR (D)
            // =========================
            int idEliminar = seleccionado.getId();
            String nombreEliminado = seleccionado.getNombre();

            boolean eliminado = productoService.eliminar(idEliminar);
            if (eliminado) {
                listaObservable.remove(seleccionado);
                actualizarMetricasDashboard();

                // Si el producto eliminado estaba siendo editado en el formulario, limpiarlo
                if (modoEdicion && productoSeleccionadoParaEdicion != null && productoSeleccionadoParaEdicion.getId() == idEliminar) {
                    restablecerFormularioAModoNuevo();
                }

                mostrarNotificacion(TipoNotificacion.EXITO, "Producto '" + nombreEliminado + "' (ID #" + idEliminar + ") eliminado correctamente.");
            } else {
                mostrarNotificacion(TipoNotificacion.ERROR, "No se pudo encontrar el producto para eliminar.");
            }
        }
    }

    /**
     * Cancela el modo de edición y regresa al modo de nuevo producto.
     */
    @FXML
    public void cancelarEdicion(ActionEvent event) {
        restablecerFormularioAModoNuevo();
        mostrarNotificacion(TipoNotificacion.INFO, "Edición cancelada.");
    }

    /**
     * Limpia la caja de búsqueda en tiempo real.
     */
    @FXML
    public void limpiarBusqueda(ActionEvent event) {
        txtBuscar.clear();
        txtBuscar.requestFocus();
    }

    // ==========================================
    // MÉTODOS DE SOPORTE Y UX
    // ==========================================

    /**
     * Carga un producto en los campos del formulario y activa el modo de edición.
     */
    private void cargarProductoEnFormulario(Producto producto) {
        if (producto == null) return;

        modoEdicion = true;
        productoSeleccionadoParaEdicion = producto;

        txtId.setText(String.valueOf(producto.getId()));
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtCantidad.setText(String.valueOf(producto.getCantidad()));

        limpiarTodosLosErrores();

        // Actualización de estados visuales UX
        lblModoFormulario.setText("✏️ Modo Edición (ID #" + producto.getId() + ")");
        lblModoFormulario.getStyleClass().setAll("card-badge-edit");

        btnGuardar.setText("💾 Actualizar Cambios");
        btnGuardar.getStyleClass().setAll("btn", "btn-update");

        btnCancelar.setDisable(false);
        txtNombre.requestFocus();

        mostrarNotificacion(TipoNotificacion.INFO, "Editando: " + producto.getNombre() + ". Modifique los valores y presione 'Actualizar Cambios'.");
    }

    /**
     * Restablece el formulario al estado de inserción ("Modo Nuevo").
     */
    private void restablecerFormularioAModoNuevo() {
        modoEdicion = false;
        productoSeleccionadoParaEdicion = null;

        limpiarCamposFormulario();
        limpiarTodosLosErrores();

        lblModoFormulario.setText("➕ Modo Nuevo");
        lblModoFormulario.getStyleClass().setAll("card-badge-create");

        btnGuardar.setText("💾 Guardar Producto");
        btnGuardar.getStyleClass().setAll("btn", "btn-save");

        btnCancelar.setDisable(true);
        tablaProductos.getSelectionModel().clearSelection();
    }

    /**
     * Limpia el texto de las cajas de entrada del formulario.
     */
    private void limpiarCamposFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtCantidad.clear();
    }

    /**
     * Valida integralmente los campos del formulario y proporciona feedback visual al usuario.
     *
     * @return true si todos los campos son válidos; false si hay errores.
     */
    private boolean validarFormulario() {
        boolean esValido = true;
        limpiarTodosLosErrores();

        // 1. Validación de Nombre
        String nombre = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
        if (nombre.isEmpty()) {
            mostrarErrorCampo(txtNombre, lblErrorNombre, "El nombre del producto es obligatorio.");
            esValido = false;
        } else if (nombre.length() < 3) {
            mostrarErrorCampo(txtNombre, lblErrorNombre, "El nombre debe contener al menos 3 caracteres.");
            esValido = false;
        }

        // 2. Validación de Precio
        String precioTexto = txtPrecio.getText() != null ? txtPrecio.getText().trim().replace(",", ".") : "";
        if (precioTexto.isEmpty()) {
            mostrarErrorCampo(txtPrecio, lblErrorPrecio, "El precio es obligatorio.");
            esValido = false;
        } else {
            try {
                double precio = Double.parseDouble(precioTexto);
                if (precio <= 0) {
                    mostrarErrorCampo(txtPrecio, lblErrorPrecio, "El precio debe ser un número mayor a 0.");
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                mostrarErrorCampo(txtPrecio, lblErrorPrecio, "Ingrese un precio decimal válido (ej. 19.99).");
                esValido = false;
            }
        }

        // 3. Validación de Cantidad
        String cantidadTexto = txtCantidad.getText() != null ? txtCantidad.getText().trim() : "";
        if (cantidadTexto.isEmpty()) {
            mostrarErrorCampo(txtCantidad, lblErrorCantidad, "La cantidad en stock es obligatoria.");
            esValido = false;
        } else {
            try {
                int cantidad = Integer.parseInt(cantidadTexto);
                if (cantidad < 0) {
                    mostrarErrorCampo(txtCantidad, lblErrorCantidad, "La cantidad no puede ser negativa.");
                    esValido = false;
                }
            } catch (NumberFormatException e) {
                mostrarErrorCampo(txtCantidad, lblErrorCantidad, "Ingrese un número entero válido (ej. 10).");
                esValido = false;
            }
        }

        return esValido;
    }

    /**
     * Muestra mensaje y borde de error en un campo específico.
     */
    private void mostrarErrorCampo(TextField campo, Label etiquetaError, String mensaje) {
        if (!campo.getStyleClass().contains("text-field-error")) {
            campo.getStyleClass().add("text-field-error");
        }
        etiquetaError.setText(mensaje);
        etiquetaError.setVisible(true);
        etiquetaError.setManaged(true);
    }

    /**
     * Remueve el estilo de error de un campo individual.
     */
    private void limpiarErrorCampo(TextField campo, Label etiquetaError) {
        campo.getStyleClass().remove("text-field-error");
        etiquetaError.setText("");
        etiquetaError.setVisible(false);
        etiquetaError.setManaged(false);
    }

    /**
     * Limpia los estilos y etiquetas de error de todos los campos.
     */
    private void limpiarTodosLosErrores() {
        limpiarErrorCampo(txtNombre, lblErrorNombre);
        limpiarErrorCampo(txtPrecio, lblErrorPrecio);
        limpiarErrorCampo(txtCantidad, lblErrorCantidad);
    }

    /**
     * Muestra una notificación visual en el banner del formulario y en el pie de página.
     */
    private void mostrarNotificacion(TipoNotificacion tipo, String mensaje) {
        bannerNotificacion.setVisible(true);
        bannerNotificacion.setManaged(true);
        lblMensajeEstado.setText(mensaje);
        lblEstadoPie.setText(mensaje);

        bannerNotificacion.getStyleClass().removeAll("banner-success", "banner-error", "banner-info");

        switch (tipo) {
            case EXITO:
                bannerNotificacion.getStyleClass().add("banner-success");
                break;
            case ERROR:
                bannerNotificacion.getStyleClass().add("banner-error");
                break;
            case INFO:
            default:
                bannerNotificacion.getStyleClass().add("banner-info");
                break;
        }
    }
}
