package edu.upc.dsa.modelos;

import java.util.HashMap;


public class User {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private int monedas;
    private boolean emailVerificado;
    private String codigoVerificacion;
    //private HashMap<String, Integer> inventario;

    public User() {
        //this.inventario = new HashMap<>();
    }

    public User(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.monedas = 0;
        this.emailVerificado = false;
        //this.inventario = new HashMap<>();
    }

    /*public HashMap<String, Integer> getInventario() {
        return inventario;
    }

    public void setInventario(HashMap<String, Integer> inventario) {
        this.inventario = inventario;
    }

    public void addObjetoInventario(String nombreObj, int cantidad){
        if(this.inventario.containsKey(nombreObj)){
            int cantidadActual = this.inventario.get(nombreObj);
            this.inventario.put(nombreObj, cantidadActual + cantidad);
        }
        else{
            this.inventario.put(nombreObj, cantidad);
        }
    }*/

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
}

