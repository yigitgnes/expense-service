package com.atech.calculator.resource;

import com.atech.calculator.model.Expense;
import com.atech.calculator.model.dto.MonthlySalesDataDTO;
import com.atech.calculator.service.ExpenseService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;

@Path("/expense")
@Tag(name = "Expense Resource", description = "Expense REST APIs")
public class ExpenseResource {

    private Logger LOGGER = Logger.getLogger(ExpenseResource.class);

    @Inject
    ExpenseService expenseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Fetches all expenses from the database.",
            summary = "Get Expenses"
    )
    public Response getAllExpenses() {
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            if (expenses.isEmpty()) {
                return Response.status(Response.Status.OK).entity("No expenses found").build();
            }
            return Response.ok(expenses).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching all expenses", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Fetches specified expense from the database.",
            summary = "Get Expense"
    )
    public Response getExpenseById(@PathParam("id") Long id){
        if (id <= 0 || id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ID provided").build();
        }
        try {
            Expense expense = expenseService.getExpenseById(id);
            if (expense == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Expense not found").build();
            }
            return Response.ok(expense).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching expense by ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Post New Expense",
            description = "Saves a new Expense into the database."
    )
    public Response createExpense(@RequestBody Expense expense) {
        Expense createdExpense = expenseService.createExpense(expense);
        URI createdUri = UriBuilder.fromResource(ExpenseResource.class)
                .path(String.valueOf(createdExpense.id))
                .build();
        return Response.created(createdUri).entity(createdExpense).build();
    }

    @PUT
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Update An Expense",
            description = "Updates an existing Expense, if the expense is exist"
    )
    public Response updateExpense(@RequestBody Expense receivedExpense) {
        if (receivedExpense.id == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try{
            expenseService.updateExpense(receivedExpense);
        }catch (NotFoundException exception){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(receivedExpense).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Deletes the Expense by the given ID",
            summary = "Delete the Expense"
    )
    public Response deleteExpense(@PathParam("id") Long id) {
        try {
            boolean isDeleted = expenseService.deleteExpense(id);
            if (isDeleted) {
                return Response.ok().entity("{\"message\":\"Expense successfully deleted.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Expense not found.\"}").build();
            }
        } catch (Exception e) {
            // Log the exception details here
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"An unexpected error occurred.\"}").build();
        }
    }
    @GET
    @Path("/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyExpenseForCurrentYear() {
        try {
            List<MonthlySalesDataDTO> monthlySalesDataDTOS = expenseService.getMonthlyExpenseForCurrentYear();
            if (monthlySalesDataDTOS.isEmpty()) {
                return Response.status(Response.Status.OK).entity("No record found").build();
            }
            return Response.ok(monthlySalesDataDTOS).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching data", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

}
