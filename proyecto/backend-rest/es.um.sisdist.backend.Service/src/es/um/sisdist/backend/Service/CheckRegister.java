package es.um.sisdist.backend.Service;

import java.util.Optional;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.models.UserDTO;
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
        System.out.println("EL CORREO: "+uo.getEmail()+" EL NOMBRE: "+uo.getName()+" LA CONTRASEÑA: "+uo.getPassword());
        Optional<User> u = impl.register(uo.getEmail(), uo.getName(), uo.getPassword());
        
        System.out.println("EL OPTIONAL: "+u);
        
        if (u.isEmpty()) {
        	System.out.println("REGISTRO FORBIDEN");
        	return Response.status(Status.FORBIDDEN).build();
        }else {
        	System.out.println("REGISTRO PERMITIDO");
        	return Response.status(Status.CREATED).build();
    	}
            


    }
	
}
