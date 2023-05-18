package es.um.sisdist.backend.restexterno.main;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

@ApplicationPath("/")
public class External extends Application {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }
    
    
}
