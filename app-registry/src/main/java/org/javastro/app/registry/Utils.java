package org.javastro.app.registry;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.ivoa.dm.tool.Tool;

import java.util.List;

/**
 * Utility methods used throughout the application
 */
public class Utils {

    /**
     * Returns a 400 Bad Request response with the supplied message
     * @param message Any message to be returned to the client
     * @return Response containing the supplied message
     */
    public static Response errorResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    /**
     * Checks to see if a tool with the supplied toolId already exists
     * @param toolId Tool.tool_id
     * @return The Tool if found, null if not
     */
    public static Tool findToolInstance(EntityManager em, String toolId) {
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

    /**
     * Performs the supplied query (with or without the pagination parameters)
     * @param page zero-indexed page index
     * @param size number of entries per page
     * @param query query to perform
     * @return Response containing HTTP response code and expected body if successful.
     */
    public static Response performQuery(Integer page, Integer size, TypedQuery<Tool> query) {
        try {
            if (page != null && size != null) {
                int firstResult = page * size;
                query.setFirstResult(firstResult);
                query.setMaxResults(size);
            }

            List<Tool> tools = query.getResultList();
            return Response.ok(tools).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
