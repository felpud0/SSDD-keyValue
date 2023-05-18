package es.um.sisdist.backend.restexterno.main;

import java.util.Optional;

import es.um.sisdist.backend.dao.models.User;
import impl.ExternalServiceImpl;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthFilter  implements ContainerRequestFilter{


    @Override
    public void filter(ContainerRequestContext requestContext) {

        // Obtenemos la cabecera de User
        String userHeader = requestContext.getHeaderString("User");

        //Obtenemos la cabecera de Date
        String dateHeader = requestContext.getHeaderString("Date");

        //Obtenemos la cabecera de Auth-Token (urlpeticion+date+usertoken)
        String authTokenHeader = requestContext.getHeaderString("Auth-Token");
        Optional<User> oUser = ExternalServiceImpl.getInstance().getUser(userHeader);

        if (userHeader == null || dateHeader == null || authTokenHeader == null || !oUser.isPresent()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        User user = oUser.get();
        boolean check = ExternalServiceImpl.getInstance().checkAuthToken(authTokenHeader, user, dateHeader, requestContext.getUriInfo().getPath());

        if (!check) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
    }


}