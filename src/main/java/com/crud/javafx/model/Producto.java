package com.crud.javafx.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

/**
 * Entidad de dominio que representa un Producto en el inventario.
 * Utiliza propiedades observables de JavaFX para permitir Data Binding
 * reactivo y actualización automática en componentes UI como TableView.
 */
public class Producto {

    private final IntegerProperty id;
    private final StringProperty nombre;
    private final DoubleProperty precio;
    private final IntegerProperty cantidad;

    /**
     * Constructor por defecto requerido para instanciación flexible.
     */
    public Producto() {
        this(0, "", 0.0, 0);
    }

    /**
     * Constructor para nuevos productos antes de asignarles un ID definitivo.
     *
     * @param nombre   Nombre o descripción del producto.
     * @param precio   Precio unitario del producto (mayor a 0).
     * @param cantidad Cantidad disponible en stock (mayor o igual a 0).
     */
    public Producto(String nombre, Double precio, Integer cantidad) {
        this(0, nombre, precio, cantidad);
    }

    /**
     * Constructor completo para inicializar un producto con su identificador.
     *
     * @param id       Identificador único del producto.
     * @param nombre   Nombre o descripción del producto.
     * @param precio   Precio unitario.
     * @param cantidad Cantidad de unidades en stock.
     */
    public Producto(Integer id, String nombre, Double precio, Integer cantidad) {
        this.id = new SimpleIntegerProperty(id != null ? id : 0);
        this.nombre = new SimpleStringProperty(nombre != null ? nombre.trim() : "");
        this.precio = new SimpleDoubleProperty(precio != null ? precio : 0.0);
        this.cantidad = new SimpleIntegerProperty(cantidad != null ? cantidad : 0);
    }

    // ==========================================
    // GETTERS, SETTERS Y PROPERTY GETTERS (JavaFX)
    // ==========================================

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre != null ? nombre.trim() : "");
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public double getPrecio() {
        return precio.get();
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    /**
     * Calcula el subtotal monetario total para este producto (precio * cantidad).
     *
     * @return Importe acumulado de las unidades en inventario.
     */
    public double getSubtotal() {
        return getPrecio() * getCantidad();
    }

    /**
     * Crea una copia profunda del producto para evitar modificaciones indeseadas
     * antes de confirmar las operaciones de actualización.
     *
     * @return Nueva instancia con los mismos valores.
     */
    public Producto clonar() {
        return new Producto(this.getId(), this.getNombre(), this.getPrecio(), this.getCantidad());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return getId() == producto.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", precio=" + getPrecio() +
                ", cantidad=" + getCantidad() +
                '}';
    }
}
