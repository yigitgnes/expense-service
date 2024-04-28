package com.atech.calculator.model;

import jakarta.enterprise.context.RequestScoped;

import java.math.BigDecimal;

@RequestScoped
public class Profit {
    public BigDecimal totalEarning;
    public BigDecimal totalStaticSpending;
    public BigDecimal totalNonStaticSpending;
    public BigDecimal profit;

    public Profit() {
    }

    public BigDecimal getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(BigDecimal totalEarning) {
        this.totalEarning = totalEarning;
    }

    public BigDecimal getTotalStaticSpending() {
        return totalStaticSpending;
    }

    public void setTotalStaticSpending(BigDecimal totalStaticSpending) {
        this.totalStaticSpending = totalStaticSpending;
    }

    public BigDecimal getTotalNonStaticSpending() {
        return totalNonStaticSpending;
    }

    public void setTotalNonStaticSpending(BigDecimal totalNonStaticSpending) {
        this.totalNonStaticSpending = totalNonStaticSpending;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    @Override
    public String toString() {
        return "Profit{" +
                "totalEarning=" + totalEarning +
                ", totalStaticSpending=" + totalStaticSpending +
                ", totalNonStaticSpending=" + totalNonStaticSpending +
                ", profit=" + profit +
                '}';
    }
}
