package es.um.sisdist.backend.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.Pair;
import es.um.sisdist.models.D;
import es.um.sisdist.models.DBDTO;
import es.um.sisdist.models.DBDTOUtils;
import es.um.sisdist.models.MapReduceRequest;
import es.um.sisdist.models.QueryResponse;
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

    /**
     *
     */
    private static final int DEFAULT_PERPAGE = 50;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDB(@PathParam("username") String username, DBDTO keyValues) {
        System.out.println(keyValues.toString());
        if (keyValues.getDbname() == null || keyValues.getDbname().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
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
        if (keyValues.getDbname() == null || keyValues.getDbname().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("DB name cannot be empty").build();
        }
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
        if (key == null || key.isBlank() || value == null || value.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Key or value cannot be empty").build();
        }
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

    @POST
    @Path("/{dbname}/d")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDBKey(@PathParam("username") String username, @PathParam("dbname") String dbname, Map<String, String> keyValues) {
        System.out.println("CREATE DB KEY: " + username + " " + dbname + " " + keyValues.toString());

        if (keyValues.get("k") == null || keyValues.get("k").isBlank() || keyValues.get("v") == null || keyValues.get("v").isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Key or value cannot be empty").build();
        }

        if (AppLogicImpl.getInstance().addKeyValue(username, dbname, keyValues.get("k"), keyValues.get("v")) ){
            //Return created with URL to the new resource
            URI uri = URI.create("/u/" + username + "/db/"+dbname+"/d/"+keyValues.get("k"));
            return Response.created(uri).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{dbname}/q")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDB( @PathParam("username") String username, @PathParam("dbname") String dbname, 
    @QueryParam("pattern") String pattern, @QueryParam("page") int page, @QueryParam("perpage") int perpage) {
       
        int nEntries = perpage == 0 ? DEFAULT_PERPAGE : perpage;
        System.out.println("QUERY DB: " + username + " " + dbname + " " + pattern + " " + page + " " + nEntries);

        //Conseguimos los pares que coinciden con el patrón
        List<Pair> pairs = AppLogicImpl.getInstance().queryDB(username, dbname, pattern);
        List<List<Pair>> pages = pairs.stream().collect(Collectors.groupingBy(s -> (pairs.indexOf(s) / perpage))).values().stream().collect(Collectors.toList());

        List<D> pageData;
        if (pages.isBlank()) {
            System.out.println("No hay resultados");
            pageData = new ArrayList<D>();
        }
        else
            pageData =  pages.get(page-1).stream().map(p -> new D(p.getKey(), p.getValue())).collect(Collectors.toList());

        QueryResponse body = new QueryResponse();
        body.pattern = pattern;
        body.dbname = dbname;
        body.page = page;
        body.perpage = nEntries;
        body.d = pageData;
        body.totalPages = pages.size();
        return Response.ok(body).build();
    }

    @POST
    @Path("/{dbname}/mr")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response mapReduceDB(@PathParam("username") String username, @PathParam("dbname") String dbname, MapReduceRequest mapReduceRequest) {
        System.out.println("MAP REDUCE DB: " + username + " " + dbname + " " + mapReduceRequest.toString());
        if (mapReduceRequest.out_db == null || mapReduceRequest.out_db.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("out_db cannot be empty").build();
        }
        String mrID =  username + "_" + dbname + "_" + mapReduceRequest.out_db + "_" + UUID.randomUUID().toString();
        AppLogicImpl.getInstance().addMapReduce(username, dbname, mapReduceRequest.map, mapReduceRequest.reduce, mapReduceRequest.out_db, mrID);
        //Return created with URL to the new resource
        return Response.accepted().location(URI.create("/u/" + username + "/db/"+dbname+"/mr/"+mrID)).build();    
    }

    @GET
    @Path("/{dbname}/mr/{mrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMapReduce( @PathParam("username") String username, @PathParam("dbname") String dbname, @PathParam("mrid") String mrid) {
        System.out.println("GET MAP REDUCE: " + username + " " + dbname + " " + mrid);
        //TODO: Comprobar la MR se ha hecho
        return Response.ok().build();
    }

    


}
