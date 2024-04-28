package com.atech.calculator.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sale")
public class Sale extends PanacheEntity {
    @Column(name = "purchase_price")
    public BigDecimal purchasePrice;

    @Column(name = "sale_price")
    @Nullable
    public BigDecimal salePrice;

    @Column(name = "purchase_date")
    public Instant purchaseDate;

    @Column(name = "sale_date")
    @Nullable
    public Instant saleDate;

    public Sale() {
    }
}
