package es.um.sisdist.backend.Service;

import java.net.URI;
import java.util.Map;
import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.models.DBDTO;
import es.um.sisdist.models.DBDTOUtils;
import es.um.sisdist.models.UserDTO;
import es.um.sisdist.models.UserDTOUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/u/{username}/db")
public class DBEndpoint {

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDB(@PathParam("username") String username, DBDTO keyValues) {
        System.out.println(keyValues.toString());
        AppLogicImpl.getInstance().addDB(username, keyValues);
        URI uri = URI.create("/u/" + username + "/db/"+keyValues.getDbname());
        return Response.created(uri).entity(keyValues).build();
    }

    @GET
    @Path("/{dbname}")
    @Produces(MediaType.APPLICATION_JSON)
    public DBDTO getDB( @PathParam("username") String username, @PathParam("dbname") String dbname) {
        System.out.println("GET DB: " + username + " " + dbname);
        DBDTO db= DBDTOUtils.toDTO(AppLogicImpl.getInstance().getDB(username, dbname).get());
        return db;
    }

    @DELETE
    @Path("/{dbname}")
    public Response deleteDB(@PathParam("username") String username, @PathParam("dbname") String dbname) {
        System.out.println("DELETE DB: " + username + " " + dbname);
        if (AppLogicImpl.getInstance().deleteDB(username, dbname) ){
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{dbname}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDB(@PathParam("username") String username, @PathParam("dbname") String dbname, DBDTO keyValues) {
        System.out.println("UPDATE DB: " + username + " " + dbname);
        if (AppLogicImpl.getInstance().updateDB(username, dbname, keyValues) ){
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{dbname}/d/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDBKey( @PathParam("username") String username, @PathParam("dbname") String dbname, @PathParam("key") String key) {
        System.out.println("GET DB KEY: " + username + " " + dbname + " " + key);
        return Response.ok(AppLogicImpl.getInstance().getKeyValue(username, dbname, key)).build();
    }

    @PUT
    @Path("/{dbname}/d/{key}")
    public Response updateDBKey(@PathParam("username") String username, @PathParam("dbname") String dbname, @PathParam("key") String key, @QueryParam("v") String value) {
        System.out.println("UPDATE DB KEY: " + username + " " + dbname + " " + key + " " + value);
        if (AppLogicImpl.getInstance().updateKeyValue(username, dbname, key, value) ){
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{dbname}/d/{key}")
    public Response deleteDBKey(@PathParam("username") String username, @PathParam("dbname") String dbname, @PathParam("key") String key) {
        System.out.println("DELETE DB KEY: " + username + " " + dbname + " " + key);
        if (AppLogicImpl.getInstance().deleteKeyValue(username, dbname, key) ){
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }





}
