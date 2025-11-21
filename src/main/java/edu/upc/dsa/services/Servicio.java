package edu.upc.dsa.services;

import edu.upc.dsa.UserManager;
import edu.upc.dsa.UserManagerImpl;
import edu.upc.dsa.modelos.User;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/usuarios", description = "Servicios de usuarios")
@Path("/usuarios")
public class Servicio {

    private UserManager m = UserManagerImpl.getInstance();

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Registrar nuevo usuario", notes = "Crea un nuevo usuario si el email no existe")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Usuario creado correctamente"),
            @ApiResponse(code = 400, message = "Datos de usuario inválidos o incompletos"),
            @ApiResponse(code = 409, message = "Email ya registrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response registrarUsuario(User u) {
        try {
            //Campos NULL
            if (u == null || u.getEmail() == null || u.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Faltan datos obligatorios (email o contraseña)").build();
            }

            //Campos vacíos (" ")
            if(u.getEmail().trim().isEmpty() || u.getPassword().trim().isEmpty()){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Los campos obligatorios no pueden estar vacíos.").build();
            }

            //Formato del Email
            String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            if(!u.getEmail().matches(EMAIL_REGEX)){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("El formato del email no es válido.").build();
            }
            User nuevo = m.registrarUsuario(u.getNombre(), u.getEmail(), u.getPassword());

            if (nuevo == null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("El email ya está registrado").build();
            }

            return Response.status(Response.Status.CREATED).entity(nuevo).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Iniciar sesión", notes = "Verifica las credenciales del usuario (email y password)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login exitoso", response = User.class),
            @ApiResponse(code = 400, message = "Faltan email o contraseña"),
            @ApiResponse(code = 401, message = "Credenciales inválidas (email o contraseña incorrectos)"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response loginUsuario(User u) {
        try {
            if (u == null || u.getEmail() == null || u.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Faltan email o contraseña").build();
            }

            User usuarioLogueado = m.loginUsuario(u.getEmail(), u.getPassword());

            if (usuarioLogueado == null) {
                return Response.status(Response.Status.UNAUTHORIZED) // 401 Unauthorized
                        .entity("Credenciales inválidas").build();
            }

            // Si el login es exitoso, se devuelve el usuario
            return Response.status(Response.Status.OK).entity(usuarioLogueado).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Obtener usuario por email", notes = "Devuelve el usuario si existe")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario encontrado"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response getUsuario(@PathParam("email") String email) {
        try {
            User u = m.getUsuario(email);
            if (u == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuario no encontrado").build();
            }
            return Response.ok(u).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }
}
