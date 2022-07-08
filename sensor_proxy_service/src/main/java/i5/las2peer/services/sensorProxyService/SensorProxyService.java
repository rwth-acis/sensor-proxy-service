package i5.las2peer.services.sensorProxyService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.influxdb.exceptions.InfluxException;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.sensorProxyService.pojo.bitalino.BitalinoData;
import i5.las2peer.services.sensorProxyService.pojo.moodmetric.MoodmetricData;
import i5.las2peer.services.sensorProxyService.writer.InfluxWriter;
import i5.las2peer.services.sensorProxyService.writer.MobSOSWriter;
import io.swagger.annotations.*;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.logging.Level;

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
	private final Gson gson = new GsonBuilder().serializeNulls().create();
	private final MobSOSWriter mobSOSWriter = new MobSOSWriter();

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

		try {
			InfluxWriter writer = new InfluxWriter();

			// check which data is provided
			if (properDataJSON.has("rawMeasurement") && !properDataJSON.getJSONArray("rawMeasurement").isEmpty()) {
				logger.info("Received moodmetric data");
				MoodmetricData moodmetricData = gson.fromJson(dataJSON.toString(), MoodmetricData.class);

				// write to influxdb
				writer.writeMoodmetric(moodmetricData);

				// write to mobSOS if evaluation is provided
				if (!moodmetricData.getMoodEvaluation().isEmpty()) {
					mobSOSWriter.write(moodmetricData, properDataJSON);
				}
			} else {
				logger.info("Received bitalino data");
				BitalinoData bitalinoData = gson.fromJson(dataJSON.toString(), BitalinoData.class);

				// write to influxdb
				writer.writeBitalino(bitalinoData);

				// write to mobSOS if evaluation is provided
				if (!bitalinoData.getMoodEvaluation().isEmpty()) {
					mobSOSWriter.write(bitalinoData, properDataJSON);
				}
			}
			writer.close();
		} catch (JsonSyntaxException jse) {
			logger.severe("Format of request data is wrong:" + jse);
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity("{\"msg\": \"Wrong data formulation.\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		} catch (InfluxException ie) {
			logger.severe(ie.toString());
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"msg\": \"Error sending to influxdb.\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		} catch (Exception e) {
			logger.severe(e.toString());
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("{\"msg\": \"Unknown error occurred.\"}")
					.type(MediaType.APPLICATION_JSON)
					.build();
		}

		String returnString = "{\"msg\": \"Statement successfully created.\"}";
		logger.info("Request response is: " + returnString);
		return Response
				.status(Response.Status.OK)
				.entity(returnString)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}