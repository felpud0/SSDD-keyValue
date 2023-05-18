package es.um.sisdist.backend.restexterno.test;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationPath("/")
public class Test {
	
	@GET
	@Path("/test")
	@Produces ( MediaType . TEXT_PLAIN )
	public String sayPlainTextHello () {
		return " TEST ";
	}

}