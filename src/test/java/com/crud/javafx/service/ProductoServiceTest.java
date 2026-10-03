package com.crud.javafx.service;

import com.crud.javafx.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para verificar la correcta implementación
 * de las operaciones CRUD en memoria (ArrayList) en {@link ProductoService}.
 */
class ProductoServiceTest {

    private IProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService();
    }

    @Test
    @DisplayName("Debe cargar datos iniciales en el ArrayList al instanciar el servicio")
    void testCargaInicial() {
        List<Producto> lista = service.listarTodos();
        assertNotNull(lista, "La lista no debe ser nula");
        assertEquals(5, lista.size(), "Deben existir 5 productos precargados de prueba");
        assertEquals(5, service.contarTotal());
    }

    @Test
    @DisplayName("Operación C (Create): Debe crear un producto y asignarle un ID correlativo")
    void testCrearProducto() {
        Producto nuevo = new Producto("Monitor Gamer Curvo 34''", 450.00, 8);
        Producto creado = service.crear(nuevo);

        assertNotNull(creado);
        assertTrue(creado.getId() > 0, "El ID generado debe ser mayor a cero");
        assertEquals("Monitor Gamer Curvo 34''", creado.getNombre());
        assertEquals(6, service.contarTotal(), "El total de productos debe incrementarse a 6");

        Optional<Producto> encontrado = service.buscarPorId(creado.getId());
        assertTrue(encontrado.isPresent(), "El nuevo producto debe ser localizable en el ArrayList");
    }

    @Test
    @DisplayName("Operación R (Read): Debe buscar productos existentes por su ID")
    void testBuscarPorId() {
        Optional<Producto> producto = service.buscarPorId(1);
        assertTrue(producto.isPresent());
        assertEquals(1, producto.get().getId());
        assertEquals("Laptop Lenovo ThinkPad T14", producto.get().getNombre());

        Optional<Producto> noExiste = service.buscarPorId(9999);
        assertFalse(noExiste.isPresent(), "Un ID inexistente debe retornar Optional.empty()");
    }

    @Test
    @DisplayName("Operación U (Update): Debe actualizar los datos de un producto existente")
    void testActualizarProducto() {
        Producto modificado = new Producto(2, "Monitor LG UltraGear 27'' Modificado", 310.50, 18);
        boolean actualizado = service.actualizar(modificado);

        assertTrue(actualizado, "La actualización debe retornar true para un ID existente");

        Optional<Producto> resultado = service.buscarPorId(2);
        assertTrue(resultado.isPresent());
        assertEquals("Monitor LG UltraGear 27'' Modificado", resultado.get().getNombre());
        assertEquals(310.50, resultado.get().getPrecio());
        assertEquals(18, resultado.get().getCantidad());
    }

    @Test
    @DisplayName("Operación D (Delete): Debe eliminar un producto existente del ArrayList")
    void testEliminarProducto() {
        int idAEliminar = 3;
        boolean eliminado = service.eliminar(idAEliminar);

        assertTrue(eliminado, "Debe retornar true si el elemento fue eliminado");
        assertEquals(4, service.contarTotal(), "La lista debe tener un elemento menos");

        Optional<Producto> busqueda = service.buscarPorId(idAEliminar);
        assertFalse(busqueda.isPresent(), "El producto eliminado ya no debe existir en el ArrayList");

        boolean eliminadoInexistente = service.eliminar(9999);
        assertFalse(eliminadoInexistente, "Eliminar un ID inexistente debe retornar false");
    }

    @Test
    @DisplayName("Cálculos de Inventario: Debe totalizar unidades y valor monetario correctamente")
    void testCalculosInventario() {
        int stockEsperado = 15 + 25 + 40 + 30 + 12; // 122 unidades
        assertEquals(stockEsperado, service.calcularStockTotal());

        double valorEsperado = (1250.00 * 15) + (289.99 * 25) + (95.50 * 40) + (119.00 * 30) + (349.99 * 12);
        assertEquals(valorEsperado, service.calcularValorInventario(), 0.001);
    }

    @Test
    @DisplayName("Filtrado por texto: Debe encontrar productos por coincidencia en nombre o ID")
    void testFiltradoPorTexto() {
        List<Producto> filtroSony = service.filtrarPorTexto("sony");
        assertEquals(1, filtroSony.size());
        assertEquals("Auriculares Sony WH-1000XM5", filtroSony.get(0).getNombre());

        List<Producto> filtroId = service.filtrarPorTexto("1");
        assertFalse(filtroId.isEmpty(), "Debe encontrar coincidencias numéricas de ID");

        List<Producto> filtroVacio = service.filtrarPorTexto("");
        assertEquals(5, filtroVacio.size(), "Un filtro vacío debe retornar todos los registros");
    }
}
