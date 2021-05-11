package i5.las2peer.services.sensorProxyService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import i5.las2peer.api.Context;
import i5.las2peer.api.logging.MonitoringEvent;
import i5.las2peer.api.security.UserAgent;
import i5.las2peer.classLoaders.Logger;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

import org.json.JSONObject;

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
	private final static L2pLogger logger = L2pLogger.getInstance(SensorProxyService.class.getName());
	
	public SensorProxyService() {
		L2pLogger.setGlobalConsoleLevel(Level.INFO);
	}
	
	/**
	 * Main functionality function. Receives data in JSON form from app,
	 * makes xAPI-Statements from it and forwards them to MobSOS.
	 * 
	 * @param dataJSON The data in JSON form.
	 * @return Returns an HTTP response confirming a successful post to the LRS.
	 */
	@POST
	@Path("/sendStatement")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "Statement has been sent to the LRS.") })
	@ApiOperation(
			value = "Send statement to LRS",
			notes = "Receives data in JSON form from app, makes xAPI-Statements from it and sends it on to the LRS.")
	public Response sendStatementsToLRS(net.minidev.json.JSONObject dataJSON) {
		logger.info("Received request.");
		// For some reason the net.minidev.json.JSONObject has to be used as the parameter
		JSONObject properDataJSON = new JSONObject(dataJSON.toJSONString());
		
		StatementGenerator generator = new StatementGenerator();
		JSONObject statement = generator.createStatementFromAppData(properDataJSON);
		
		if (statement == null) {
			logger.warning("Format of request data is wrong.");
			String returnString = "{\"msg\": \"Wrong data formulation.\"}";
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(returnString)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		else {			
			String eventMessage = statement.toString() + "*" + dataJSON.getAsString("userID");
			logger.info("Created statement: " + statement.toString());
			logger.info("Forwarding statement to MobSOS with token: " + dataJSON.getAsString("userID"));
			Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_1, eventMessage);
			Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_2, properDataJSON.toString());
		}
		
		
		String returnString = "{\"msg\": \"Statement succesfully created.\"}";
		logger.info("Request response is: " + returnString);
		return Response
				.status(Response.Status.OK)
				.entity(returnString)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

}
