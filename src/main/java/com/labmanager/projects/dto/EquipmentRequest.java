package com.labmanager.projects.dto;

public class EquipmentRequest {
    private String name;
    private int stock;

    public EquipmentRequest() {}

    public EquipmentRequest(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }
}
