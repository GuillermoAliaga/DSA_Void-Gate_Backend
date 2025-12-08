package edu.upc.dsa.modelos;

public class ObjetoCompra {
    private int userId;
    private int itemId;

    // Constructor vacío
    public ObjetoCompra() {}

    public ObjetoCompra(int userId, int itemId) {

        this.userId = userId;
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
