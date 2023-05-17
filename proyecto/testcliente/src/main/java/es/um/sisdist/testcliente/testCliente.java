package es.um.sisdist.testcliente;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

/**
 * Hello world!
 *
 */
public class testCliente {

	@Test
	public void testRegistro() {
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

}
