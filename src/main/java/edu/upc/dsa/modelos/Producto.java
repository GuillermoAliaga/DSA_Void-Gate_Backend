package edu.upc.dsa.modelos;

public class Producto {
    private int id;
    private String nombre;
    private int precio;

    // 1. Constructor vacío (OBLIGATORIO para el ORM)
    public Producto() {
    }

    // 2. Constructor para crear productos nuevos
    // NOTA: No pasamos ID porque es auto-increment en la BBDD
    public Producto(String nombre, int precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    // GETTERS Y SETTERS
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

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }
}