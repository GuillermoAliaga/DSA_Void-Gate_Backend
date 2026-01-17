package edu.upc.dsa.services;

import edu.upc.dsa.UserManager;
import edu.upc.dsa.UserManagerImpl;
import edu.upc.dsa.modelos.User;
import edu.upc.dsa.modelos.Verificacion;
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
            boolean enviado = m.enviarCodigoVerificacion(nuevo);

            if (!enviado) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error al enviar código de verificación").build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity("Usuario creado. Revisa tu email para el código de verificación.").build();


        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno del servidor: " + e.getMessage()).build();
        }
    }
    @POST
    @Path("/verificar-codigo")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Verificar código de email", notes = "Valida el código de 6 dígitos enviado")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email verificado correctamente"),
            @ApiResponse(code = 400, message = "Código incorrecto o expirado"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })
    public Response verificarCodigo(Verificacion req) {
        try {
            if (req == null || req.getEmail() == null || req.getCodigo() == null ||
                    req.getEmail().trim().isEmpty() || req.getCodigo().trim().isEmpty()) {

                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Email y código son obligatorios\"}")
                        .build();
            }

            String email = req.getEmail();
            String codigo = req.getCodigo();

            User usuario = m.getUsuario(email);

            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado\"}")
                        .build();
            }

            boolean verificado = m.verificarCodigo(email, codigo);

            if (!verificado) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Código incorrecto\"}")
                        .build();
            }

            return Response.status(Response.Status.OK)
                    .entity("{\"mensaje\":\"Email verificado correctamente. Ya puedes iniciar sesión.\"}")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
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
            @ApiResponse(code = 403, message = "Email no verificado"),
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
            if (!usuarioLogueado.getEmailVerificado()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Por favor verifica tu email antes de iniciar sesión.").build();
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

    @PUT
    @Path("/monedas/{email}/{cantidad}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Modificar monedas (Dev/Test)", notes = "Permite establecer las monedas de un usuario manualmente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Monedas actualizadas"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno")
    })
    public Response updateMonedas(@PathParam("email") String email, @PathParam("cantidad") int cantidad) {
        try {
            User u = m.getUsuario(email);
            if (u == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuario no encontrado").build();
            }

            u.setMonedas(cantidad);

            return Response.status(Response.Status.OK)
                    .entity("Monedas actualizadas a: " + cantidad).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno: " + e.getMessage()).build();
        }
    }
    @PUT
    @Path("/puntos/{id}/{cantidad}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Sumar puntos al usuario", notes = "Añade la cantidad especificada al total de puntos del usuario")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Puntos actualizados"),
            @ApiResponse(code = 404, message = "Usuario no encontrado"),
            @ApiResponse(code = 500, message = "Error interno")
    })
    public Response sumarPuntos(@PathParam("id") int id, @PathParam("cantidad") int cantidad) {
        try {
            int total = m.sumarPuntos(id, cantidad); // 'm' es tu UserManager

            if (total >= 0) {
                return Response.status(Response.Status.OK)
                        .entity("{\"mensaje\":\"Puntos actualizados\", \"total\":" + total + "}")
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
