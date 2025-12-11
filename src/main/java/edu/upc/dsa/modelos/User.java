package edu.upc.dsa.modelos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List; // <--- IMPRESCINDIBLE PARA QUE FUNCIONE LA LISTA

public class User {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private int monedas;
    private boolean emailVerificado;
    private String codigoVerificacion;

    // --- NUEVO CAMPO PARA EL JSON ---
    // Esto guardará los objetos que recuperamos en el UserManager
    private transient List<Inventory> inventario;

    public User() {
        // Inicializamos la lista vacía para evitar errores de "NullPointerException"
        this.inventario = new LinkedList<>();
    }

    public User(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.monedas = 1000;
        this.emailVerificado = false;
        this.inventario = new LinkedList<>(); // Inicializamos aquí también
    }

    // --- GETTERS Y SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public int getMonedas() {
        return monedas;
    }

    public void setMonedas(int monedas) {
        this.monedas = monedas;
    }

    // --- NUEVOS MÉTODOS PARA EL INVENTARIO ---
    @JsonProperty("inventario")
    public List<Inventory> getInventario() {
        return inventario;
    }

    public void setInventario(List<Inventory> inventario) {
        this.inventario = inventario;
    }
}