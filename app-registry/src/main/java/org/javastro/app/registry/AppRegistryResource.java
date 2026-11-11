package org.javastro.app.registry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
    @Produces({MediaType.APPLICATION_JSON})
    public Response getAllTools(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        if ((page != null) ^ (size != null)) {
            return errorResponse("Both page and size must be specified or neither.");
        } else if ((page != null && page < 0) || (size != null && size < 1)) {
            return errorResponse("Invalid pagination parameters. 'page' must be >= 0 and 'size' must be > 0.");
        }

        TypedQuery<Tool> query = em.createQuery("SELECT t FROM Tool t", Tool.class);
        return performQuery(page, size, query);
    }

    @POST
    @Path("/tools")
    @Transactional
    public Response addTool(Tool tool) {
        Tool existingTool = findTool(em, tool.getTool_id());
        if (existingTool != null) {
            return Response.status(Response.Status.CONFLICT).build();
        } else {
            em.persist(tool);
            em.flush();
            return Response.ok().build();
        }
    }

    /**
     * Performs the supplied query (with or without the pagination parameters)
     * @param page zero-indexed page index
     * @param size number of entries per page
     * @param query query to perform
     * @return Response containing HTTP response code and expected body if successful.
     */
    private static Response performQuery(Integer page, Integer size, TypedQuery<Tool> query) {
        try {
            if (page != null && size != null) {
                int firstResult = page * size;
                query.setFirstResult(firstResult);
                query.setMaxResults(size);
            }

            List<Tool> tools = query.getResultList();
            //ObservationListWrapper wrapper = new ObservationListWrapper(observations);

            return Response.ok(/*wrapper*/tools).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Checks to see if a tool with the supplied toolId already exists
     * @param toolId Tool.tool_id
     * @return The Tool if found, null if not
     */
    private Tool findTool(EntityManager em, String toolId) {
        TypedQuery<Tool> existsQuery = em.createQuery(
                "SELECT t FROM Tool t WHERE t.tool_id = :toolId", Tool.class
        );

        try {
            existsQuery.setParameter("toolId", toolId);
            return existsQuery.getSingleResult();
        } catch (NoResultException e){
            return null;
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

    private Response errorResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }
}
