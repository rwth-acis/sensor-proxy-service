package i5.las2peer.services.sensorProxyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import i5.las2peer.api.Context;
import i5.las2peer.api.security.UserAgent;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import net.minidev.json.JSONObject;

/**
 * sensor-proxy-service
 * 
 * The Sensor Proxy Service serves to facilitate communication between the sensor app (Moodmetric) and
 * a designated Learning Records Store. 
 * 
 */
@Api
@SwaggerDefinition(
		info = @Info(
				title = "Sensor Proxy Service",
				version = "0.1.0",
				description = "A las2peer service that allows the gathering, processing and forwarding sensor data to an LRS.",
				contact = @Contact(
						name = "Boris Jovanovic",
						email = "jovanovic.boris@rwth-aachen.de")))
@ServicePath("/sensorProxy")
public class SensorProxyService extends RESTService {
	// TODO: Put this in a environment variable
	private String lrsEndpoint = "https://lrs.tech4comp.dbis.rwth-aachen.de/data/xAPI/statements";
	private String lrsClientAuth = "Basic NGU3Zjg5NTZiODVkYzc2MzBkNTJlYzdiMDkzOGJlYmZmOGM2ZDdlYToyODIwMGQ1MTUzYTUyZGY1MDcwZmI3OTJiNTA4NTg3NjljZjFlMWZl";
	

	/**
	 * Template of a get function.
	 * 
	 * @return Returns an HTTP response with the username as string content.
	 */
	@GET
	@Path("/get")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(
			value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME",
			notes = "REPLACE THIS WITH YOUR NOTES TO THE FUNCTION")
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "REPLACE THIS WITH YOUR OK MESSAGE") })
	public Response getTemplate() {
		return Response.ok("Excellent.").build();
	}

	/**
	 * Template of a post function.
	 * 
	 * @param dataJSON The post input the user will provide.
	 * @return Returns an HTTP response with plain text string content derived from the path input param.
	 */
	@POST
	@Path("/sendStatement")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "Statement has been sent to the LRS.") })
	@ApiOperation(
			value = "REPLACE THIS WITH AN APPROPRIATE FUNCTION NAME",
			notes = "Example method that returns a phrase containing the received input.")
	public Response sendStatementsToLRS(JSONObject dataJSON) {
		try {
			URL lrsURL = new URL(lrsEndpoint);
			HttpURLConnection connection =  (HttpURLConnection) lrsURL.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("X-Experience-API-Version", "1.0.3");
			connection.setRequestProperty("Authorization", lrsClientAuth);
			connection.setDoOutput(true);
			
			
			try(OutputStream os = connection.getOutputStream()) {
			    byte[] input = dataJSON.toJSONString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		    StringBuilder responseString = new StringBuilder();
		    String responseLine = null;
		    while ((responseLine = br.readLine()) != null) {
		    	responseString.append(responseLine.trim());
		    }
		    System.out.println(responseString.toString());
		    
		    return Response.ok(responseString.toString()).build();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String returnString = "";
		returnString += "Input " + dataJSON.toJSONString();
		return Response.ok().entity(returnString).build();
	}

}
