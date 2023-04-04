package es.um.sisdist.backend.Service;

import java.util.Optional;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/registerUsr")
public class CheckRegister {

	private AppLogicImpl impl = AppLogicImpl.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkUser(UserDTO uo)
    {
        Optional<User> u = impl.register(uo.getEmail(), uo.getName(), uo.getPassword());
        System.out.println("EL CORREO: "+uo.getEmail()+" EL NOMBRE: "+uo.getName()+" LA CONTRASEÑA: "+uo.getPassword());
        if (u.isPresent())
            return Response.status(Status.FORBIDDEN).build();
    	else {
    		
    		return Response.ok(u).build();
    	}
            


    }
	
}
