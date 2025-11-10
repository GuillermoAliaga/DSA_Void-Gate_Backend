package edu.upc.dsa;

import edu.upc.dsa.modelos.User;
import java.util.List;

public interface UserManager {
    User registrarUsuario(String nombre, String email, String password);
    User loginUsuario(String email, String password);
    User getUsuario(String email);
    List<User> getUsuarios();
}
