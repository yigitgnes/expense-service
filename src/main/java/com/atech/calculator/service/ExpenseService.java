package com.atech.calculator.service;

import com.atech.calculator.model.Expense;
import com.atech.calculator.model.dto.MonthlySalesDataDTO;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExpenseService {

    private static final int currentYear = Year.now().getValue();

    @PersistenceContext
    EntityManager entityManager;

    private Logger LOGGER = Logger.getLogger(ExpenseService.class);

    public List<Expense> getAllExpenses() throws Exception {
        try {
            List<Expense> expenses = Expense.findAll(Sort.by("id")).list();
            if (expenses.isEmpty()) {
                LOGGER.info("No expenses found in the database.");
            }
            return expenses;
        } catch (Exception e) {
            LOGGER.error("Error retrieving all expenses: " + e.getMessage(), e);
            throw new Exception("Error retrieving expenses from the database.", e);
        }
    }

    public List<Expense> getAllExpensesPaged(int page, int size) {
        return Expense.findAll(Sort.by("expenseDate").descending()).page(Page.of(page, size)).list();
    }

    public long countExpenses (){
        return Expense.count();
    }

    public Expense getExpenseById(Long id) throws NotFoundException {
        try {
            Expense expense = Expense.findById(id);
            if (expense == null) {
                LOGGER.info("No expense found with ID: " + id);
                throw new NotFoundException("Expense with ID " + id + " not found.");
            }
            return expense;
        } catch (Exception e) {
            LOGGER.error("Error retrieving expense by ID " + id + ": " + e.getMessage(), e);
            throw new RuntimeException("Internal server error when fetching expense by ID.", e);
        }
    }

    public Expense createExpense(Expense expense) {
        // Validate the input
        if (expense.name == null || expense.name.trim().isEmpty() || expense.price == null) {
            LOGGER.info("Validation failed: Price or Name cannot be empty for the expenses.");
            throw new BadRequestException("Price or Name cannot be empty.");
        }
        try {
            expense.persistAndFlush();
            LOGGER.info("A new expense is created: " + expense.name);
            return expense;
        } catch (Exception e) {
            LOGGER.error("Error creating expense: " + e.getMessage());
            throw new RuntimeException("Error creating expense.", e);
        }
    }

    public void updateExpense(Expense receivedExpense){
        Expense.<Expense>findByIdOptional(receivedExpense.id).ifPresentOrElse(
                expense -> {
                    expense.name = receivedExpense.name;
                    expense.price = receivedExpense.price;
                    expense.description = receivedExpense.description;
                    expense.expenseDate = receivedExpense.expenseDate;
                    expense.persist();
                },
                NotFoundException::new
        );
    }

    public List<MonthlySalesDataDTO> getMonthlyExpenseForCurrentYear(){
        // Get all expenses from the EXPENSE table
        String queryStr = "SELECT EXTRACT(MONTH FROM e.expenseDate) AS expense_month, SUM(e.price) AS total_price " +
                "FROM Expense e " +
                "WHERE EXTRACT(YEAR FROM e.expenseDate) = ?1 " +
                "GROUP BY EXTRACT(MONTH FROM e.expenseDate) " +
                "ORDER BY EXTRACT(MONTH FROM e.expenseDate)";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter(1, currentYear);
        List<Object[]> expenseResult = query.getResultList();

        // Get all expenses from the SALE table
        String queryStr1 = "SELECT EXTRACT(MONTH FROM s.purchaseDate) AS expense_month, SUM(s.purchasePrice) AS total_price " +
                "FROM Sale s " +
                "WHERE EXTRACT(YEAR FROM s.purchaseDate) = ?1 " +
                "GROUP BY EXTRACT(MONTH FROM s.purchaseDate) " +
                "ORDER BY EXTRACT(MONTH FROM s.purchaseDate)";
        Query query1 = entityManager.createQuery(queryStr1);
        query1.setParameter(1, currentYear);
        List<Object[]> saleResult = query1.getResultList();

        // Combine the results
        Map<Integer, Long> monthlyExpenses = new HashMap<>();

        // Process expense results
        for (Object[] result : expenseResult) {
            int month = ((Number) result[0]).intValue();
            long price = ((Number) result[1]).longValue();
            monthlyExpenses.put(month, price);
        }

        // Process sale results and add to existing monthly expenses
        for (Object[] result : saleResult) {
            int month = ((Number) result[0]).intValue();
            long price = ((Number) result[1]).longValue();
            monthlyExpenses.merge(month, price, Long::sum);
        }

        // Convert to list of MonthlySalesDataDTO
        return monthlyExpenses.entrySet().stream()
                .map(entry -> {
                    int month = entry.getKey();
                    long totalExpense = entry.getValue();
                    String formattedMonth = Month.of(month)
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    return new MonthlySalesDataDTO(formattedMonth, totalExpense);
                })
                .sorted(Comparator.comparing(dto -> Month.valueOf(dto.getMonth().toUpperCase())))
                .collect(Collectors.toList());
    }

    public boolean deleteExpense(Long id){
        return Expense.deleteById(id);
    }
}
