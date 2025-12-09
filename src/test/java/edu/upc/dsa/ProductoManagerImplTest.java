package edu.upc.dsa;

import edu.upc.dsa.modelos.Producto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ProductoManagerImplTest {

    // CAMBIO CLAVE 1: Usamos la Interfaz, no la clase 'Impl'
    private ProductoManager productoManager;

    @Before
    public void setUp() {
        // Obtenemos la instancia del Singleton
        productoManager = ProductoManagerImpl.getInstance();

        // Limpiamos la lista para empezar de cero en cada test
        // (Si usas BBDD, aquí deberías hacer un DELETE FROM productos)
        List<Producto> productos = productoManager.getProductos();
        if (productos != null) {
            productos.clear();
        }
    }

    @After
    public void tearDown() {
        // Opcional: Limpieza extra al acabar
        if (productoManager.getProductos() != null) {
            productoManager.getProductos().clear();
        }
    }

    @Test
    public void testAnadirProductoExitoso() {
        // Al usar la interfaz, esto ya no da error de tipos
        Producto p = productoManager.addProducto("Poción de Vida", 50);

        // Verificaciones
        assertNotNull("El producto no debe ser null", p);
        // Nota: Si usas BBDD, el ID quizás no se asigna hasta guardar.
        // Si es en memoria, asegúrate de que tu addProducto asigne ID.
        // assertNotNull("El producto debe tener ID", p.getId());

        assertEquals("El nombre debe coincidir", "Poción de Vida", p.getNombre());
        assertEquals("El precio debe coincidir", 50, p.getPrecio(), 0.001); // El delta es necesario para doubles

        assertEquals("Debe haber 1 producto en la lista", 1, productoManager.getProductos().size());
    }

    @Test
    public void testAnadirProductoDuplicadoNombre() {
        // 1. Añadimos el primero
        productoManager.addProducto("Poción de Vida", 50);

        // 2. Intentamos añadir el segundo con el MISMO nombre
        // Asumo que tu lógica devuelve null si está repetido (como en User)
        Producto repetido = productoManager.addProducto("Poción de Vida", 100);

        // 3. Verificamos que NO se haya creado
        assertNull("No se debe permitir añadir un producto con nombre repetido", repetido);

        // 4. Verificamos que la lista siga teniendo solo 1 elemento
        assertEquals("Debe seguir habiendo solo 1 producto", 1, productoManager.getProductos().size());
    }

    @Test
    public void testListaProductos() {
        productoManager.addProducto("Escudo", 100);
        productoManager.addProducto("Espada", 200);

        assertEquals(2, productoManager.getProductos().size());
    }
}