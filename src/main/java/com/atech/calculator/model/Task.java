package com.atech.calculator.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    @Column
    public String description;
    @Column
    @Enumerated(EnumType.STRING)
    public TaskCategory category;
    @Column
    public boolean completed;
}

