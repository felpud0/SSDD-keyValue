package es.um.sisdist.backend.restexterno.test;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class Test {

	@GET
	public Response sayPlainTextHello() {
	    AppLogicImpl impl = AppLogicImpl.getInstance();

		return Response.ok(impl.ping(1)).build();	
		}
	

}