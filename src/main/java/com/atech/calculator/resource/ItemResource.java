package com.atech.calculator.resource;

import com.atech.calculator.model.Item;
import com.atech.calculator.model.dto.MonthlySalesDataDTO;
import com.atech.calculator.service.ItemService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/item")
@Tag(name = "Item Resource", description = "Item REST APIs")
public class ItemResource {

    private Logger LOGGER = Logger.getLogger(ExpenseResource.class);
    @Inject
    ItemService itemService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Returns all the Items saved into the database",
            summary = "Get All Items"
    )
    public Response getAllItems() throws Exception {
        return Response.ok(itemService.getAllItems()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Fetches specified Item from the database.",
            summary = "Get Item"
    )
    public Response getItemById(@PathParam("id") Long id){
        if (id <= 0 || id == null){
           return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ID provided").build();
        }
        try {
            Item item = itemService.getItemById(id);
            if (item == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Item not found").build();
            }
            return Response.ok(item).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching expense by ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Post New Item",
            description = "Saves a new Item into the database."
    )
    public Response createItem(@RequestBody Item item){
        itemService.createIteam(item);
        return Response.ok(item).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
            summary = "Update An Item",
            description = "Updates an existing Item, if the item is exist"
    )
    public Response updateItem(@RequestBody Item item){
        itemService.updateItem(item);
        return Response.ok(item).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(
            summary = "Delete An Item",
            description = "Removes an Existing Item from database"
    )
    public Response deleteItem(@PathParam("id") Long id){
        try{
            boolean isDeleted = itemService.deleteItem(id);
            if (isDeleted) {
                return Response.ok().entity("{\"message\":\"Item successfully deleted.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"message\":\"Item not found.\"}").build();
            }
        } catch (Exception e) {
            // Log the exception details here
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"message\":\"An unexpected error occurred.\"}").build();
        }
    }

    @GET
    @Path("/sales/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MonthlySalesDataDTO> getMonthlySales() {
        return itemService.getMonthlySalesForCurrentYear();
    }

    @GET
    @Path("/earning/monthly")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MonthlySalesDataDTO> getMonthlyEarnings(){
        return itemService.getMonthlyEarningForCurrentYear();
    }


}
