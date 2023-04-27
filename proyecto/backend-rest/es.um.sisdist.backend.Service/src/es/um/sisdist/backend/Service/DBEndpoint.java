package es.um.sisdist.backend.Service;

import java.net.URI;
import java.util.Map;
import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.models.DBDTO;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/u/{username}/db")
public class DBEndpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDB(@PathParam("username") String username, DBDTO keyValues) {
        System.out.println(keyValues.toString());
        // Return a URI that appends to the current path
        URI uri = URI.create("/u/" + username + "/db/");
        return Response.created(uri).entity(keyValues).build();
    }

}
