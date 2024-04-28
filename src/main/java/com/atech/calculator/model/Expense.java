package com.atech.calculator.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "expense")
public class Expense extends PanacheEntity {

    @Column
    public String name;
    @Column
    public BigDecimal price;
    @Column
    @Nullable
    public String description;
    @Column(name = "expense_date")
    public Instant expenseDate;

    public Expense() {
    }
}
