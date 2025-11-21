package edu.upc.dsa.services;

import edu.upc.dsa.ProductoManager;
import edu.upc.dsa.ProductoManagerImpl;
import edu.upc.dsa.modelos.Producto;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/productos", description = "Servicios de la Tienda (Productos)")
@Path("/productos")
public class ProductoServicio {

    private ProductoManager pm;

    public ProductoServicio() {
        this.pm = ProductoManagerImpl.getInstance();

        if (pm.listadeproductos().isEmpty()) {
            pm.anadirproducto("Vendaje", 50);
            pm.anadirproducto("Machete", 200);
            pm.anadirproducto("Chaleco antibalas", 75);
        }
    }
    
    @GET

    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Obtener el listado de productos de la tienda",
            notes = "Devuelve una lista con todos los productos disponibles")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listado de productos OK", response = Producto.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response getProductos() {
        try {
            List<Producto> listaProductos = this.pm.listadeproductos();

            return Response.status(Response.Status.OK).entity(listaProductos).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Añadir un nuevo producto",
            notes = "Añade un nuevo producto a la tienda. El ID será autogenerado.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Producto creado correctamente", response = Producto.class),
            @ApiResponse(code = 400, message = "Datos de producto inválidos (falta nombre)"),
            @ApiResponse(code = 409, message = "El nombre del producto ya existe (Conflicto)"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })

    public Response anadirProducto(Producto producto) {
        try {
            if (producto == null || producto.getNombreproducto() == null || producto.getNombreproducto().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Falta el nombre del producto o el precio.").build();
            }

            if(producto.getPrecio() < 0){
                return Response.status(Response.Status.BAD_REQUEST).entity("Insertar un precio válido.").build();
            }

            Producto nuevo = this.pm.anadirproducto(producto.getNombreproducto(), producto.getPrecio());

            if (nuevo == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("El nombre del producto '" + producto.getNombreproducto() + "' ya existe.").build();
            }

            return Response.status(Response.Status.CREATED).entity(nuevo).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{nombre}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Obtener un producto por nombre",
            notes = "Busca y devuelve un producto específico dado su nombre")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Producto encontrado", response = Producto.class),
            @ApiResponse(code = 404, message = "Producto no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response getProductoPorNombre(@PathParam("nombre") String nombre) {
        try {
            Producto p = this.pm.getproducto(nombre);

            if (p == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Producto no encontrado: " + nombre).build();
            }

            return Response.status(Response.Status.OK).entity(p).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }
}
