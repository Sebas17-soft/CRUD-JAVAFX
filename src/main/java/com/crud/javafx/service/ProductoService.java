package com.crud.javafx.service;

import com.crud.javafx.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio que almacena y gestiona los datos
 * exclusivamente en memoria utilizando una estructura {@link java.util.ArrayList}.
 * No utiliza base de datos ni ningún mecanismo de persistencia externa.
 */
public class ProductoService implements IProductoService {

    /**
     * Estructura principal de almacenamiento en memoria para el ciclo de vida de la aplicación.
     */
    private final List<Producto> productos = new ArrayList<>();

    /**
     * Secuencia numérica para generar identificadores únicos de forma autoincremental.
     */
    private int secuenciaId = 1;

    /**
     * Constructor del servicio. Carga datos iniciales de demostración en el ArrayList
     * para que la interfaz muestre registros de inmediato al ejecutarse.
     */
    public ProductoService() {
        cargarDatosSemilla();
    }

    /**
     * Llena el ArrayList con registros de prueba representativos.
     */
    private void cargarDatosSemilla() {
        crear(new Producto("Laptop Lenovo ThinkPad T14", 1250.00, 15));
        crear(new Producto("Monitor LG UltraGear 27'' IPS", 289.99, 25));
        crear(new Producto("Teclado Mecánico Keychron K2", 95.50, 40));
        crear(new Producto("Mouse Inalámbrico Logitech MX Master 3S", 119.00, 30));
        crear(new Producto("Auriculares Sony WH-1000XM5", 349.99, 12));
    }

    @Override
    public List<Producto> listarTodos() {
        // Retorna una copia defensiva para proteger el ArrayList interno
        return new ArrayList<>(productos);
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        return productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    @Override
    public Producto crear(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto a registrar no puede ser nulo.");
        }
        // Asignar identificador correlativo autoincremental
        producto.setId(secuenciaId++);
        productos.add(producto);
        return producto;
    }

    @Override
    public boolean actualizar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto a actualizar no puede ser nulo.");
        }

        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == producto.getId()) {
                // Actualiza los valores en la referencia existente o reemplaza el elemento en el ArrayList
                Producto existente = productos.get(i);
                existente.setNombre(producto.getNombre());
                existente.setPrecio(producto.getPrecio());
                existente.setCantidad(producto.getCantidad());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return productos.removeIf(p -> p.getId() == id);
    }

    @Override
    public int contarTotal() {
        return productos.size();
    }

    @Override
    public int calcularStockTotal() {
        return productos.stream()
                .mapToInt(Producto::getCantidad)
                .sum();
    }

    @Override
    public double calcularValorInventario() {
        return productos.stream()
                .mapToDouble(Producto::getSubtotal)
                .sum();
    }

    @Override
    public List<Producto> filtrarPorTexto(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodos();
        }

        String termino = criterio.trim().toLowerCase();
        return productos.stream()
                .filter(p -> String.valueOf(p.getId()).contains(termino) ||
                             p.getNombre().toLowerCase().contains(termino))
                .collect(Collectors.toList());
    }
}
