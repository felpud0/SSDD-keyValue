package es.um.sisdist.backend.Service;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/u")
public class UsersEndpoint
{
    private AppLogicImpl impl = AppLogicImpl.getInstance();

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserInfo(@PathParam("username") String username)
    {
        return UserDTOUtils.toDTO(impl.getUserByEmail(username).orElse(null));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(UserDTO uo) 
    {
        System.out.println("ID: "+ uo.getId() +"EL CORREO: "+uo.getEmail()+" EL NOMBRE: "+uo.getName()+" LA CONTRASEÑA: "+uo.getPassword()); 
        User u = impl.register(uo);
        return Response.status(Status.CREATED).entity(uo).build();
    }

    @GET
    //@Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers()
    {
        System.out.println("GET USERS");
        return Response.ok(impl.getAllUsers()).build();
    }
}
