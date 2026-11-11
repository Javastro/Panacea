package org.javastro.app.registry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.ivoa.dm.tool.Tool;

import java.util.List;

@Path("/")
public class AppRegistryResource {

    @PersistenceContext
    protected EntityManager em;

    //Temporary test data
    Tool t1 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-1";
        builder.version = "1.0";
        builder.command = List.of("my-command");
    });

    Tool t2 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-2";
        builder.version = "1.0";
        builder.command = List.of("a-command");
    });

    @GET
    public String hello() {
        addTestTool(t1);
        addTestTool(t2);

        return "Hello World!";
    }

    @GET
    @Path("/tools")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTools(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        if ((page != null) ^ (size != null)) {
            return Utils.errorResponse("Both page and size must be specified or neither.");
        } else if ((page != null && page < 0) || (size != null && size < 1)) {
            return Utils.errorResponse("Invalid pagination parameters. 'page' must be >= 0 and 'size' must be > 0.");
        }

        TypedQuery<Tool> query = em.createQuery("SELECT t FROM Tool t", Tool.class);
        return Utils.performQuery(page, size, query);
    }

    @GET
    @Path("/tools/{toolId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToolById(@PathParam("toolId") String toolId) {
        Tool tool = Utils.findToolInstance(em, toolId);
        if (tool != null) {
            return Response.ok(tool).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Tool with toolId " + toolId + " not found")
                    .build();
        }
    }

    @POST
    @Path("/tools")
    @Transactional
    public Response addTool(Tool tool) {
        Tool existingTool = Utils.findToolInstance(em, tool.getTool_id());
        if (existingTool != null) {
            return Response.status(Response.Status.CONFLICT).build();
        } else {
            em.persist(tool);
            em.flush();
            return Response.ok().build();
        }
    }

    @PUT
    @Path("/tools/{toolId}")
    @Transactional
    public Response updateTool(@PathParam("toolId") String toolId, Tool tool) {
        try {
            if (toolId == null || toolId.isEmpty()) {
                return Utils.errorResponse("Invalid toolId");
            } else if (!toolId.equals(tool.getTool_id())) {
                return Utils.errorResponse("toolId MUST be the same as tool.getTool_id()");
            }

            Tool existing = Utils.findToolInstance(em, toolId);
            if (existing != null) {
                BeanUtils.copyProperties(existing, tool);
                return Response.ok(existing).build();
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Tool not found")
                    .build();
        } catch (Exception e) {
            return Utils.errorResponse("Error updating tool: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/tools/{toolId}")
    @Transactional
    public Response deleteTool(@PathParam("toolId") String toolId) {
        Tool tool = Utils.findToolInstance(em, toolId);
        if (tool != null) {
            em.remove(tool);
            em.flush();
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.TEXT_PLAIN)
                    .entity("Tool with toolId " + toolId + " not found")
                    .build();
        }
    }

    //To be replaced by the test data that's currently in the ExecutionDM unit test
    private void addTestTool(Tool tool) {
        try (Response response = addTool(tool)) {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                System.out.println("Tool added successfully.");
            } else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                System.out.println("Tool already exists.");
            } else {
                System.out.println("Unexpected response status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
