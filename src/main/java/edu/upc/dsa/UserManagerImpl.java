package edu.upc.dsa;

import edu.upc.dsa.modelos.User;
import edu.upc.dsa.dao.*;
import org.apache.log4j.Logger;

import java.util.*;

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

        // 1. Crear objeto Java
        u = new User(nombre, email, password);

        // 2. Generar y asignar código
        String codigo = String.valueOf((int) (Math.random() * 900000) + 100000);
        u.setCodigoVerificacion(codigo);
        u.setEmailVerificado(false);

        try {
            session = FactorySession.openSession();
            session.save(u);

            logger.info("Usuario registrado: " + email);
            return u;

        } catch (Exception e) {
            logger.error("Error al registrar: " + e.getMessage());
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public User loginUsuario(String email, String password) {
        Session session = null;
        try {
            session = FactorySession.openSession();
            // Recuperamos todos los usuarios (o podrías hacer un select por email si tu DAO lo permite)
            List<Object> usersList = session.findAll(User.class);

            for (Object obj : usersList) {
                User u = (User) obj;
                if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                    logger.info("Login exitoso: " + email);

                    // NOTA: YA NO CARGAMOS EL INVENTARIO AQUÍ.
                    // El frontend lo pedirá por separado usando el ID del usuario.

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
            List<Object> allUsers = session.findAll(User.class);

            if (allUsers != null) {
                for (Object obj : allUsers) {
                    User u = (User) obj;
                    if (u.getEmail().equals(email)) {
                        usuarioEncontrado = u;
                        break;
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
        Session session = null;
        List<User> lista = new ArrayList<>();
        try {
            session = FactorySession.openSession();
            List<Object> rawList = session.findAll(User.class);

            if (rawList != null) {
                for (Object o : rawList) {
                    lista.add((User) o);
                }
            }
        } catch (Exception e) {
            logger.warn("Error o lista vacía: " + e.getMessage());
        } finally {
            if (session != null) session.close();
        }
        return lista;
    }

    public boolean enviarCodigoVerificacion(User u) {
        Session session = null;
        try {
            // Lógica de seguridad por si el código viene nulo
            if (u.getCodigoVerificacion() == null || u.getCodigoVerificacion().isEmpty()) {
                String nuevoCodigo = String.valueOf((int) (Math.random() * 900000) + 100000);
                u.setCodigoVerificacion(nuevoCodigo);
            }

            session = FactorySession.openSession();
            session.update(u);

            System.out.println("📧 CÓDIGO DE VERIFICACIÓN para " + u.getEmail() + ": " + u.getCodigoVerificacion());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean verificarCodigo(String email, String codigoUsuario) {
        Session session = null;
        try {
            User u = this.getUsuario(email);

            if (u == null) {
                logger.warn("Usuario no encontrado: " + email);
                return false;
            }

            if (u.getCodigoVerificacion() != null && u.getCodigoVerificacion().equals(codigoUsuario)) {
                u.setEmailVerificado(true);

                session = FactorySession.openSession();
                session.update(u);

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

    public void eliminarUsuario(String email) {
        Session session = null;
        try {
            User usuarioReal = this.getUsuario(email);

            if (usuarioReal != null) {
                session = FactorySession.openSession();
                session.delete(usuarioReal);
                logger.info("Usuario eliminado correctamente de BBDD: " + email);
            } else {
                logger.warn("Intentando borrar usuario inexistente: " + email);
            }
        } catch (Exception e) {
            logger.error("Error al eliminar usuario: " + e.getMessage());
        } finally {
            if (session != null) session.close();
        }
    }
}