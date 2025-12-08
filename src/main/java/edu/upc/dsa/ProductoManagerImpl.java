package edu.upc.dsa;

import edu.upc.dsa.dao.FactorySession;
import edu.upc.dsa.dao.Session;
import edu.upc.dsa.modelos.Producto;
import edu.upc.dsa.modelos.User;
import edu.upc.dsa.modelos.Inventory;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class ProductoManagerImpl implements ProductoManager {
    final static Logger logger = Logger.getLogger(ProductoManagerImpl.class);
    private static ProductoManagerImpl instance;

    public static ProductoManager getInstance() {
        if (instance == null) instance = new ProductoManagerImpl();
        return instance;
    }

    private ProductoManagerImpl() {}

    // 1. Obtener todos los productos
    @Override
    public List<Producto> getProductos() {
        Session session = null;
        List<Producto> lista = new LinkedList<>();
        try {
            session = FactorySession.openSession();
            // Traemos todo sin filtrar (tu ORM no soporta parámetros aquí)
            List<Object> objetos = session.findAll(Producto.class);

            for (Object obj : objetos) {
                lista.add((Producto) obj);
            }
        } catch (Exception e) {
            logger.error("Error al obtener productos: " + e.getMessage());
        } finally {
            if (session != null) session.close();
        }
        return lista;
    }

    // 2. Obtener producto por nombre (Iterando la lista manualmente)
    @Override
    public Producto getProducto(String nombre) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            List<Object> objetos = session.findAll(Producto.class);

            for (Object obj : objetos) {
                Producto p = (Producto) obj;
                if (p.getNombre().equals(nombre)) {
                    return p;
                }
            }
        } catch (Exception e) {
            logger.error("Error buscando producto " + nombre + ": " + e.getMessage());
        } finally {
            if (session != null) session.close();
        }
        return null;
    }

    // 3. Añadir producto (SIN DESCRIPCIÓN)
    @Override
    public Producto addProducto(String nombre, int precio) {
        // 1. Verificamos si ya existe (para el error 409 Conflict del servicio)
        if (this.getProducto(nombre) != null) {
            logger.warn("El producto " + nombre + " ya existe.");
            return null;
        }

        Session session = null;
        try {
            session = FactorySession.openSession();
            Producto p = new Producto(nombre, precio);
            session.save(p);
            logger.info("Producto añadido: " + nombre);
            return p; // Retornamos el objeto para que el servicio devuelva 201 Created
        } catch (Exception e) {
            logger.error("Error al añadir producto: " + e.getMessage());
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    // 4. Comprar Producto
    @Override
    public int comprarProducto(int itemId, int userId) {
        Session session = null;
        try {
            session = FactorySession.openSession();

            // Recuperamos usuario y producto
            // Usamos session.get si tu ORM lo soporta por ID
            User usuario = (User) session.get(User.class, userId);
            Producto producto = (Producto) session.get(Producto.class, itemId);

            // CASE 1: Usuario no existe (Devuelve 404 en servicio)
            if (usuario == null) {
                logger.warn("Usuario " + userId + " no encontrado.");
                return 1;
            }

            // CASE 2: Producto no existe (Devuelve 404 en servicio)
            if (producto == null) {
                logger.warn("Producto " + itemId + " no encontrado.");
                return 2;
            }

            // CASE 3: Saldo insuficiente (Devuelve 402 en servicio)
            if (usuario.getMonedas() < producto.getPrecio()) {
                logger.warn("Usuario " + userId + " sin saldo suficiente.");
                return 3;
            }

            // CASE 0: ÉXITO (Devuelve 201 en servicio)

            // A. Restar dinero
            int nuevoSaldo = usuario.getMonedas() - producto.getPrecio();
            usuario.setMonedas(nuevoSaldo);
            session.update(usuario);

            // B. Añadir al inventario
            Inventory registro = new Inventory(userId, itemId, 1);
            session.save(registro);

            logger.info("Compra realizada: User " + userId + " -> Item " + itemId);
            return 0; // Todo OK

        } catch (Exception e) {
            logger.error("Error crítico en comprarProducto: " + e.getMessage());
            return 500; // Error desconocido
        } finally {
            if (session != null) session.close();
        }
    }
}