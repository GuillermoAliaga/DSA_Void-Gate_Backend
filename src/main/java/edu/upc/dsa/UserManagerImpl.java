package edu.upc.dsa;

import edu.upc.dsa.modelos.User;
import java.util.*;
import edu.upc.dsa.dao.*;
import org.apache.log4j.Logger;

public class UserManagerImpl implements UserManager {
    private static UserManagerImpl instance;
    private List<User> usuarios;
    final static Logger logger = Logger.getLogger(UserManagerImpl.class);

    private UserManagerImpl() {
        usuarios = new ArrayList<>();
    }

    public static UserManagerImpl getInstance() {
        if (instance == null) instance = new UserManagerImpl();
        return instance;
    }

    @Override
    public User registrarUsuario(String nombre, String email, String password) {
        Session session = null;
        User u = null;
        try {
            session = FactorySession.openSession();

            // 1. Crear objeto Java
            u = new User(nombre, email, password);

            // 2. Generar y asignar código AQUÍ (Antes de tocar la BBDD)
            // Esto elimina la necesidad de usar funciones complejas de update luego.
            String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
            u.setCodigoVerificacion(codigo);
            u.setEmailVerificado(false); // O el valor numérico 0 si usas int

            // 3. Guardar en BBDD (INSERT)
            // Como ya lleva el código, se guarda todo junto.
            session.save(u);

            logger.info("Usuario registrado: " + email);
            return u;

        } catch (Exception e) {
            // Capturar duplicados
            if (e.getMessage() != null && e.getMessage().contains("Duplicate")) {
                logger.error("El email ya existe: " + email);
            } else {
                logger.error("Error al registrar: " + e.getMessage());
            }
            return null;
        } finally {
            // 4. Cerrar sesión sin usar isOpen() (solo verificamos null)
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public User loginUsuario(String email, String password) {
        // Buscamos al usuario por email
       /* User u = this.getUsuario(email);

        if (u != null) {
            // Si el usuario existe, se comprueba la contraseña
            if (u.getPassword().equals(password)) {
                return u; //Login correcto
            } else {
                return null; //Contraseña incorrecta
            }
        }
        return null; // Usuario no encontrado*/
        Session session = null;
        try {
            session = FactorySession.openSession();

            // Como no tenemos 'findByEmail', traemos todos y filtramos
            // (No es lo más eficiente pero es lo estándar en este nivel de ORM)
            List<Object> usersList = session.findAll(User.class);

            for (Object obj : usersList) {
                User u = (User) obj;
                if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                    logger.info("Login exitoso: " + email);
                    return u;
                }
            }
        } catch (Exception e) {
            logger.error("Error en login: " + e.getMessage());
        } finally {
            if (session != null) session.close();
        }

        logger.warn("Usuario o password incorrectos: " + email);
        return null;
    }

    public User getUsuario(String email) {
        Session session = null;
        User usuarioEncontrado = null;
        try {
            session = FactorySession.openSession();

            // 1. SOLUCIÓN: Usar findAll con 1 solo argumento (trae todo)
            // Esto elimina el error "Expected 1 argument but found 2"
            List<Object> allUsers = session.findAll(User.class);

            // 2. Buscamos manualmente en la lista devuelta
            if (allUsers != null) {
                for (Object obj : allUsers) {
                    User u = (User) obj;
                    if (u.getEmail().equals(email)) {
                        usuarioEncontrado = u;
                        break; // Ya lo tenemos, salimos del bucle
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error buscando usuario: " + e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return usuarioEncontrado;
    }

    @Override
    public List<User> getUsuarios() {
        return usuarios;
    }

    // En UserManagerImpl.java

    public boolean enviarCodigoVerificacion(User u) {
        Session session = null;
        try {
            // 1. Generar el código
            int code = (int) (Math.random() * 900000) + 100000;
            String codigoStr = String.valueOf(code);

            // 2. Asignar al objeto en memoria
            u.setCodigoVerificacion(codigoStr);

            // 3. Actualizar la BBDD
            session = FactorySession.openSession();
            session.update(u); // Asegúrate de que tu ORM tiene .update()

            System.out.println("═══════════════════════════════════════");
            System.out.println("📧 CÓDIGO DE VERIFICACIÓN");
            System.out.println("Email: " + u.getEmail());
            System.out.println("Código: " + codigoStr);
            System.out.println("═══════════════════════════════════════");
            return true; // ÉXITO

        } catch (Exception e) {
            logger.error("Error al generar/guardar código para " + u.getEmail() + ": " + e.getMessage());
            return false; // FALLO
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean verificarCodigo(String email, String codigoUsuario) {
        Session session = null;
        try {
            // 1. Obtener usuario desde la BBDD (Tendremos el ID correcto)
            User u = this.getUsuario(email);

            if (u == null) {
                logger.warn("Usuario no encontrado: " + email);
                return false;
            }

            // 2. Comparar códigos
            if (u.getCodigoVerificacion() != null && u.getCodigoVerificacion().equals(codigoUsuario)) {

                // 3. Modificar objeto
                u.setEmailVerificado(true); // O set(1)

                // 4. Actualizar en BBDD
                session = FactorySession.openSession();
                session.update(u); // Esto funcionará porque 'u' tiene ID al venir del 'findAll'

                logger.info("Usuario validado: " + email);
                return true;
            }

            logger.warn("Código incorrecto.");
            return false;

        } catch (Exception e) {
            logger.error("Error verificando: " + e.getMessage());
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
