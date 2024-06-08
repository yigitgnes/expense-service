package com.atech.calculator.service;

import com.atech.calculator.model.Expense;
import com.atech.calculator.model.Item;
import com.atech.calculator.model.Profit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProfitService {

    @Inject
    Logger logger;
    @Inject
    ExpenseService expenseService;
    @Inject
    ItemService itemService;
    BigDecimal spending;
    BigDecimal earning;
    public Profit getProfit() throws Exception {
        Profit profit = new Profit();
        profit.setTotalStaticSpending(countStaticExpenses());
        BigDecimal itemProfit = countItemProfit(profit);
        profit.setProfit(itemProfit.subtract(profit.getTotalStaticSpending()));
        BigDecimal overallSpending = profit.getTotalNonStaticSpending().add(profit.getTotalStaticSpending());
        logger.info("Total overall spending: " + overallSpending);
        return profit;
    }

    private BigDecimal countStaticExpenses() throws Exception {
        spending = BigDecimal.valueOf(0);
        List<Expense> expenses = expenseService.getAllExpenses();
        for (Expense expense: expenses) {
            spending = spending.add(expense.price);
        }
        logger.info("Total spending for static expenses: " + spending);
        return spending;
    }

    private BigDecimal countItemProfit(Profit profit) throws Exception {
        spending = BigDecimal.valueOf(0);
        earning = BigDecimal.valueOf(0);
        List<Item> items = itemService.getAllItems();
        for (Item item : items){
            spending = spending.add(item.sale.purchasePrice);
            earning = earning.add(item.sale.salePrice);
        }
        logger.info("Total earning from Items: " + earning);
        logger.info("Total spending for Items: " + spending);
        profit.setTotalEarning(earning);
        profit.setTotalNonStaticSpending(spending);
        return earning.subtract(spending);
    }

}
