package edu.upc.dsa;

import edu.upc.dsa.modelos.User;
import java.util.*;

public class UserManagerImpl implements UserManager {
    private static UserManagerImpl instance;
    private List<User> usuarios;

    private UserManagerImpl() {
        usuarios = new ArrayList<>();
    }

    public static UserManagerImpl getInstance() {
        if (instance == null) instance = new UserManagerImpl();
        return instance;
    }

    @Override
    public User registrarUsuario(String nombre, String email, String password) {
        for (User u : usuarios) {
            if (u.getEmail().equals(email))
                return null;
        }

        User nuevo = new User(UUID.randomUUID().toString(), nombre, email, password);
        usuarios.add(nuevo);
        return nuevo;
    }

    @Override
    public User loginUsuario(String email, String password) {
        // Buscamos al usuario por email
        User u = this.getUsuario(email);

        if (u != null) {
            // Si el usuario existe, se comprueba la contraseña
            if (u.getPassword().equals(password)) {
                return u; //Login correcto
            } else {
                return null; //Contraseña incorrecta
            }
        }
        return null; // Usuario no encontrado
    }

    @Override
    public User getUsuario(String email) {
        for (User u : usuarios) {
            if (u.getEmail().equals(email)) return u;
        }
        return null;
    }

    @Override
    public List<User> getUsuarios() {
        return usuarios;
    }
}
