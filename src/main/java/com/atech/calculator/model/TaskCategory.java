package com.atech.calculator.model;

import java.io.Serializable;

public enum TaskCategory implements Serializable {
    MARKETING("marketing"),
    INVENTORY_MANAGEMENT("inventory-management"),
    CUSTOMER_ENGAGEMENT("customer-engagement");

    private String category;

    TaskCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
