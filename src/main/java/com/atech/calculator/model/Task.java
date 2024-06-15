package com.atech.calculator.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table
public class Task extends PanacheEntity {

    public String title;
    public String description;
    @Enumerated(EnumType.STRING)
    public TaskCategory category;
    public boolean completed;
}

enum TaskCategory {
    MARKETING,
    INVENTORY_MANAGEMENT,
    CUSTOMER_ENGAGEMENT
}
