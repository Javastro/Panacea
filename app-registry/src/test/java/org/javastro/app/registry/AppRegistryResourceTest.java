package org.javastro.app.registry;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.ivoa.dm.tool.Tool;
import org.ivoa.dm.tool.ToolImage;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AppRegistryResourceTest {

    @Inject
    EntityManager em;

    @Inject
    AppRegistryResource appRegistryResource;

    //400 Bad Request currently. Use TestResource OR the ExecutionDM test data if/when exposed rather than the simple Tool instances below.
    //TestResource testResource = new TestResource();

    //ToolImage baseImage = testResource.createBaseImage();
    //ToolImage image = testResource.createImage(baseImage);
    //Tool tool = testResource.createTool(image);

    //TODO Temporary test data until the model issues are resolved.
    Tool t1 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-1";
        builder.version = "1.0";
        builder.command = List.of("my-command");
    });

    Tool t2 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-2";
        builder.version = "1.1";
        builder.command = List.of("other-command");
    });

    @BeforeEach
    @AfterAll
    @Transactional
    public void clearDatabase() {
        em.createQuery("DELETE FROM Tool").executeUpdate();
    }

    @Test
    @DisplayName("Check that and empty database returns a robust response.")
    public void testEmptyDatabase() {
        var response = appRegistryResource.getAllTools(null, null);
        assert response.getStatus() == 200;
        assertInstanceOf(List.class, response.getEntity());

        List<?> tools = (List<?>) response.getEntity();
        assertTrue(tools.isEmpty());
    }

    @Test
    @DisplayName("Check that the database can be populated with test data.")
    public void testDatabasePopulation() {
        try (var response = appRegistryResource.addTool(t1)) {
            System.out.println(response.getStatus());
            assert response.getStatus() == 201;
        }
    }

    @Test
    @DisplayName("Check that the database successfully retrieves test data.")
    public void testDatabasePopulationAndRetrieval() {
        try (var response = appRegistryResource.addTool(t1);
             var response2 = appRegistryResource.addTool(t2)) {
            System.out.println(response.getStatus());
            assert response.getStatus() == 201;
            System.out.println(response2.getStatus());
            assert response2.getStatus() == 201;

            var retrievalRes = appRegistryResource.getAllTools(null, null);
            assertInstanceOf(List.class, retrievalRes.getEntity());
            List<?> tools = (List<?>) retrievalRes.getEntity();
            assertEquals(2, tools.size());
        }
    }
}
