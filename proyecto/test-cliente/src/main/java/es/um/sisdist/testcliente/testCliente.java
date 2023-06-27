package es.um.sisdist.testcliente;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import org.junit.Test;

import es.um.sisdist.backend.Service.impl.AppLogicImpl;
import es.um.sisdist.backend.dao.models.User;
import es.um.sisdist.backend.grpc.GrpcServiceGrpc;
import es.um.sisdist.backend.grpc.RPCMapReduceRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

/*
 * Hello world!
 *
 */
public class testCliente {

	@Test
	public void registroTest() {
		String email = "test1@test.com";
		String name = "Test1";
		String password = "psswd";
		String url = "http://localhost:8080/Service/u/";

		// Crear la solicitud POST
		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			// Crear el cuerpo de la solicitud
			String requestBody = String.format("{\"email\":\"%s\",\"name\":\"%s\",\"password\":\"%s\"}", email, name,
					password);

			// Enviar el cuerpo de la solicitud
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(requestBody.getBytes());
			os.flush();
			os.close();

			// Obtener la respuesta del servidor
			int responseCode = con.getResponseCode();

			// Leer la respuesta del servidor
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			assertEquals(201, responseCode);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	/*@Test
	 public void loginTest() {
		String email = "test1@test.com";
		String password = "psswd";
		String url = "http://localhost:8080/Service/checkLogin";
		URL obj;
		try {
			// Crear la solicitud POST
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			// Crear el cuerpo de la solicitud
			String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);

			// Enviar el cuerpo de la solicitud
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(requestBody.getBytes());
			os.flush();
			os.close();

			// Obtener la respuesta del servidor
			int responseCode = con.getResponseCode();

			// Leer la respuesta del servidor
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			
			assertEquals(200, responseCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test	
	public void crearBBDDTest() {
		login();
		String email = "test1@test.com";
		String nombreBBDD = "miBBDD";
		String url = "http://localhost:8080/Service/u/"+email+"/db/";
		
		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			// Crear el cuerpo de la solicitud
	        String requestBody = String.format("{\"dbname\":\"%s\"}", nombreBBDD);

			// Enviar el cuerpo de la solicitud
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(requestBody.getBytes());
			os.flush();
			os.close();

			// Obtener la respuesta del servidor
			int responseCode = con.getResponseCode();

			// Leer la respuesta del servidor
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			assertEquals(200, responseCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test	
	public void addElementoBBDDTest() {
		//login();
		String email = "test1@test.com";
		String nombreBBDD = "miBBDD";
	    String key = "testKey";
        String value = "testValue";
		
		String url = "http://localhost:8080/Service/u/" + email + "/db/" + nombreBBDD + "/d";
		
		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");

			// Crear el cuerpo de la solicitud
	        String requestBody = "{\"k\": \"" + key + "\", \"v\": \"" + value + "\"}";

			// Enviar el cuerpo de la solicitud
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(requestBody.getBytes());
			os.flush();
			os.close();

			// Obtener la respuesta del servidor
			int responseCode = con.getResponseCode();

			// Leer la respuesta del servidor
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
			assertEquals(201, responseCode);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test	
	public void removeElementoBBDDTest() {
		//login();
		String email = "test1@test.com";
		String nombreBBDD = "miBBDD";
	    String key = "testKey";
		
		String url = "http://localhost:8080/Service/u/" + email + "/db/" + nombreBBDD + "/d/" + key;

		URL obj;
		try {
			obj = new URL(url);

			HttpURLConnection con;
			con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("DELETE");

			// Obtener la respuesta del servidor
			int responseCode = con.getResponseCode();
			
			assertEquals(204, responseCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}*/
	// @Test	
	// public void mapReduceTest() {
	// 	//login();
	// 	String email = "test1@test.com";
	// 	String nombreBBDD = "miBBDD";
    //     String map = "(import \"java.lang.String\")\n(define (ssdd-map k v) (display k) (display \": \") (display v) (display \"\\n\") (for-each (lambda (w) (emit (list w 1))) (vector->list (.split v \" \"))))";
    //     String reduce = "( define ( ssdd-reduce k l ) ( apply + l ) )";
    //     String out_db = "miBBDDMR";
        
    //     // Test de grpc, puede hacerse con la BD
    // 	var msg = RPCMapReduceRequest.newBuilder()
	// 						        .setMap(map)
	// 						        .setReduce(reduce)
	// 						        .setOutDb(out_db)
	// 						        .setUser(email)
	// 						        .setInDb(nombreBBDD)
	// 						        .build( );
    	
    //     var grpcServerName = Optional.ofNullable(System.getenv("GRPC_SERVER"));
    //     var grpcServerPort = Optional.ofNullable(System.getenv("GRPC_SERVER_PORT"));
    	
    // 	ManagedChannel channel = ManagedChannelBuilder
    //             .forAddress(grpcServerName.orElse("localhost"), Integer.parseInt(grpcServerPort.orElse("50051")))
    //             .usePlaintext().build();
    	
    //     final GrpcServiceGrpc.GrpcServiceBlockingStub blockingStub = GrpcServiceGrpc.newBlockingStub(channel);
    	
    //     blockingStub.mapReduce(msg);
        
    //     assertTrue(getBBDD());
    	        
	// }
	
	// @Test
	// public void getBBDDTest() {

	// 	String email = "test1@test.com";
	// 	String nombreBBDD = "miBBDDMR";

	// 	String url = "http://localhost:8080/Service/u/" + email + "/db/" + nombreBBDD;
	// 	try {

	// 		URL obj = new URL(url);

	// 		// Abrir una conexión HTTP
	// 		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	// 		// Configurar el método de solicitud
	// 		con.setRequestMethod("GET");

	// 		// Obtener el código de respuesta
	// 		int responseCode = con.getResponseCode();

	// 		// Leer la respuesta del servidor
	// 		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	// 		String inputLine;
	// 		StringBuilder response = new StringBuilder();
	// 		while ((inputLine = in.readLine()) != null) {
	// 			response.append(inputLine);
	// 		}
	// 		in.close();

	// 		assertEquals(200, responseCode);
						
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
       
	// }
	
	// public boolean getBBDD() {

	// 	String email = "test1@test.com";
	// 	String nombreBBDD = "miBBDDMR";

	// 	String url = "http://localhost:8080/Service/u/" + email + "/db/" + nombreBBDD;
	// 	try {

	// 		URL obj = new URL(url);

	// 		// Abrir una conexión HTTP
	// 		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	// 		// Configurar el método de solicitud
	// 		con.setRequestMethod("GET");

	// 		// Obtener el código de respuesta
	// 		int responseCode = con.getResponseCode();

	// 		// Leer la respuesta del servidor
	// 		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	// 		String inputLine;
	// 		StringBuilder response = new StringBuilder();
	// 		while ((inputLine = in.readLine()) != null) {
	// 			response.append(inputLine);
	// 		}
	// 		in.close();

			
	// 		if (responseCode == 200)
	// 			return true;
	// 		return false;
			
	// 	} catch (IOException e) {
	// 		// TODO Auto-generated catch block
	// 		e.printStackTrace();
	// 	}
       
	// 	return false;
		
	// }
	

}
