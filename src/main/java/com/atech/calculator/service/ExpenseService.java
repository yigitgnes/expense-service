package com.atech.calculator.service;

import com.atech.calculator.model.Expense;
import com.atech.calculator.resource.ExpenseResource;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class ExpenseService {

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

    public boolean deleteExpense(Long id){
        return Expense.deleteById(id);
    }
}
