package com.atech.calculator.resource;

import com.atech.calculator.model.Profit;
import com.atech.calculator.service.ProfitService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/profit")
@Tag(name = "Profit Resource", description = "Profit REST APIs")
public class ProfitResource {
    private Logger LOGGER = Logger.getLogger(ProfitResource.class);
    @Inject
    ProfitService profitService;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            description = "Returns the all over profit",
            summary = "Get Profit"
    )
    public Response getProfit() {
        try {
            LOGGER.info("Calculation Process Started");
            Profit profit = profitService.getProfit();
            return Response.ok(profit).build();
        } catch (Exception e) {
            LOGGER.error("Error calculating profit", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error calculating profit").build();
        }
    }
}
