package main.java.es.um.sisdist;

import java.util.Optional;

import es.um.sisdist.backend.dao.models.User;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import main.java.es.um.sisdist.impl.ExternalServiceImpl;

@Provider
public class AuthFilter  implements ContainerRequestFilter{

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final String AUTHENTICATION_TOKEN = "YOUR_AUTHENTICATION_TOKEN";

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

        // Verifica si el token de autenticación es válido
        


        // Obtén el encabezado de autorización de la solicitud
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Verifica si el encabezado de autorización está presente y tiene el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith(AUTHENTICATION_SCHEME)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        // Extrae el token de autenticación del encabezado
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        // Verifica si el token de autenticación es válido
        if (!token.equals(AUTHENTICATION_TOKEN)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }


}
