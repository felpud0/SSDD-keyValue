package es.um.sisdist.backend.restexterno.test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
public class Test {

	@GET

	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return " TEST ";
	}

}