package com.atech.calculator.resource;

import com.atech.calculator.model.Task;
import com.atech.calculator.service.TaskService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

@Path("/tasks")
public class TaskResource {

    private Logger LOGGER = Logger.getLogger(TaskResource.class);

    @Inject
    TaskService taskService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasks(@DefaultValue("") @QueryParam("category") String category) {
        if (!category.isEmpty()){
            return Response.ok(taskService.getTasksByCategory(category)).build();
        }
        return Response.ok(taskService.getAllTasks()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskById(@PathParam("id") Long id) {
        if (id <= 0 || id == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ID provided").build();
        }
        try {
            Task task = taskService.getTaskById(id);
            if (task == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
            }
            return Response.ok(task).build();
        } catch (Exception e) {
            LOGGER.error("Error while fetching the data");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        return Response.ok(createdTask).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateTask(@PathParam("id") Long id, @RequestBody Task receivedTask) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            taskService.updateTask(id, receivedTask);
        }catch (NotFoundException exception){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(receivedTask).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return Response.noContent().build();
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
