package org.javastro.app.registry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.ivoa.dm.pdl.ParameterGroup;
import org.ivoa.dm.tool.Tool;

import java.util.List;

@Path("/")
public class AppRegistryResource {

    @PersistenceContext
    protected EntityManager em;

    Tool t1 = new Tool();

    Tool t2 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-1";
        builder.command = List.of("my-command");
    });

    public AppRegistryResource() {
        t1.setTool_id("fits-calibration");
    }


    @GET
    public Tool hello() {
        return t1;
        //return "Hello World!";
    }

    @GET
    @Path("/tools")
    public Response getAllTools() {
        TypedQuery<Tool> query = em.createQuery("SELECT t FROM Tool t", Tool.class);
        return performQuery(0, 10, query);
    }

    @POST
    @Path("/tools")
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
}
