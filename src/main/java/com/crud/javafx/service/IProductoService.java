package com.crud.javafx.service;

import com.crud.javafx.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de la capa de servicio para la gestión de productos.
 * Define las operaciones CRUD y operaciones auxiliares de consulta y cálculo.
 */
public interface IProductoService {

    /**
     * Retorna la lista completa de productos registrados en memoria.
     *
     * @return Lista inmutable o copia defensiva de los productos.
     */
    List<Producto> listarTodos();

    /**
     * Busca un producto por su identificador único.
     *
     * @param id Identificador a localizar.
     * @return Optional con el producto si existe, o Optional.empty() si no se encontró.
     */
    Optional<Producto> buscarPorId(int id);

    /**
     * Agrega un nuevo producto a la colección en memoria, asignándole un identificador único correlativo.
     *
     * @param producto Instancia del producto con sus datos validados.
     * @return El producto guardado con su nuevo ID asignado.
     */
    Producto crear(Producto producto);

    /**
     * Actualiza la información de un producto existente identificado por su ID.
     *
     * @param producto Producto con los nuevos datos a actualizar.
     * @return true si el producto existía y fue modificado exitosamente; false en caso contrario.
     */
    boolean actualizar(Producto producto);

    /**
     * Elimina un producto de la colección en memoria según su ID.
     *
     * @param id Identificador del producto a eliminar.
     * @return true si fue localizado y eliminado; false si no existía.
     */
    boolean eliminar(int id);

    /**
     * Cuenta la cantidad de productos distintos registrados.
     *
     * @return Total de productos en la lista.
     */
    int contarTotal();

    /**
     * Suma las unidades en stock de todos los productos.
     *
     * @return Total de unidades acumuladas.
     */
    int calcularStockTotal();

    /**
     * Calcula la valoración monetaria total del inventario (sumatoria de precio * cantidad).
     *
     * @return Monto total en moneda local.
     */
    double calcularValorInventario();

    /**
     * Filtra productos que coincidan con un texto en su ID o nombre.
     *
     * @param criterio Texto de búsqueda ingresado por el usuario.
     * @return Lista de productos filtrados.
     */
    List<Producto> filtrarPorTexto(String criterio);
}
