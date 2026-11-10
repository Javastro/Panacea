package org.javastro.app.registry;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.ivoa.dm.tool.Tool;

import java.util.List;

@Path("/")
public class AppRegistryResource {

    Tool t1 = new Tool();

    Tool t2 = Tool.createTool(builder -> {
        builder.tool_id = "test-tool-1";
        builder.command = List.of("my-command");
    });

    public AppRegistryResource() {
        t1.setTool_id("fits-calibration");
    }


    @GET
    public String hello() {
        return "Hello World!";
    }
}
