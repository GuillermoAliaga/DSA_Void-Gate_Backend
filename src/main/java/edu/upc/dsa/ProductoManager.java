package edu.upc.dsa;

import edu.upc.dsa.modelos.Producto;
import edu.upc.dsa.modelos.User;
import java.util.List;

public interface ProductoManager {
    List<Producto> getProductos();
    Producto getProducto (String nombreproducto);
    Producto addProducto(String nombreproducto, int precio);//pongo este por si queremos que el id venga del rest
    //Producto encontrarproducto (String nombreproducto);
    int comprarProducto (int userID, int productoID);
}
