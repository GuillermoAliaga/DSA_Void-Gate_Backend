package edu.upc.dsa.modelos;



import java.util.LinkedList;
import java.util.List; // <--- IMPRESCINDIBLE PARA QUE FUNCIONE LA LISTA

public class User {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private int monedas;
    private int puntos;
    private boolean emailVerificado;
    private String codigoVerificacion;


    public User() {
    }

    public User(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.monedas = 1000;
        this.puntos = 0;
        this.emailVerificado = false;
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
    public int getPuntos() {return puntos;}
    public void setPuntos(int puntos) {this.puntos = puntos;}
}