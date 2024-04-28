package com.atech.calculator.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "item_type")
public class Item extends PanacheEntity {

    @Column(name = "name")
    public String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    public Sale sale;

    public Item() {
    }
}
