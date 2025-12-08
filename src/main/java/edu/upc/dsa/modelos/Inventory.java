package edu.upc.dsa.modelos;

public class Inventory {
    private int id;
    private int userId;
    private int itemId; // ID del producto
    private int cantidad;

    public Inventory() {}

    public Inventory(int userId, int itemId, int cantidad) {
        this.userId = userId;
        this.itemId = itemId;
        this.cantidad = cantidad;
    }

    // Getters y Setters necesarios para el ORM
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}