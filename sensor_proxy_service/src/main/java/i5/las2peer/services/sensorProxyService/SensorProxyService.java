package i5.las2peer.services.sensorProxyService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.influxdb.exceptions.InfluxException;
import i5.las2peer.api.Context;
import i5.las2peer.api.security.UserAgent;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.sensorProxyService.pojo.context.ContextData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.bitalino.BitalinoData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric.MoodmetricData;
import i5.las2peer.services.sensorProxyService.writer.InfluxWriter;
import i5.las2peer.services.sensorProxyService.writer.MobSOSWriter;
import io.swagger.annotations.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
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
	 * forwards data to InfluxDB. If mood evaluation data is included,
	 * makes xAPI-Statements from it and forwards them to MobSOS and LRS.
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
					message = "Sensordata has been sent to InfluxDB.") })
	@ApiOperation(
			value = "Send data to InfluxDB, send update to LRS",
			notes = "Receives data in JSON form from app, sends data to InfluxDB, makes xAPI-Statements and sends it to the LRS.")
	public Response storeSensor(net.minidev.json.JSONObject dataJSON) {
		logger.info("Received request.");

		// Get user mail
		UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();
		String mail = userAgent.getEmail();

		try {
			InfluxWriter writer = new InfluxWriter(hashSHA384(mail));
			// check which data is provided
			if (dataJSON.containsKey("rawSkinResistanceMeasurements")) {
				logger.info("Received moodmetric data");
				MoodmetricData moodmetricData = gson.fromJson(dataJSON.toString(), MoodmetricData.class);

				// write to influxdb
				writer.writeMoodmetric(moodmetricData);

				// write to mobSOS if evaluation is provided
				if (!moodmetricData.getMoodEvaluations().isEmpty()) {
					// mobSoS can't handle hash, so send plain mail
					mobSOSWriter.write(moodmetricData, dataJSON, mail);
				}
			} else {
				logger.info("Received bitalino data");
				BitalinoData bitalinoData = gson.fromJson(dataJSON.toString(), BitalinoData.class);

				// write to influxdb
				writer.writeBitalino(bitalinoData);

				// write to mobSOS if evaluation is provided
				if (!bitalinoData.getMoodEvaluations().isEmpty()) {
					// mobSoS can't handle hash, so send plain mail
					mobSOSWriter.write(bitalinoData, dataJSON, mail);
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

	/**
	 * Receives context data in JSON form from app,
	 * and forwards it to InfluxDB.
	 *
	 * @param dataJSON The data in JSON form.
	 * @return Returns an HTTP response confirming a successful post to the InfluxDB.
	 */
	@POST
	@Path("/context")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(
			value = { @ApiResponse(
					code = HttpURLConnection.HTTP_OK,
					message = "Context information has been sent to the InfluxDB.") })
	@ApiOperation(
			value = "Send context information to InfluxDB",
			notes = "Receives context-data in JSON form from app, sends it to the InfluxDB.")
	public Response storeContext(net.minidev.json.JSONObject dataJSON) {
		logger.info("Received context request.");

		// Get user mail
		UserAgent userAgent = (UserAgent) Context.getCurrent().getMainAgent();

		try {
			ContextData contextData = gson.fromJson(dataJSON.toString(), ContextData.class);

			InfluxWriter writer = new InfluxWriter(hashSHA384(userAgent.getEmail()));
			writer.writeContext(contextData);
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

		String returnString = "{\"msg\": \"Context successfully submitted.\"}";
		logger.info("Request response is: " + returnString);
		return Response
				.status(Response.Status.OK)
				.entity(returnString)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

	private String hashSHA384(String text) throws java.security.NoSuchAlgorithmException{
		// Hash mail for db data allocation
		MessageDigest md = MessageDigest.getInstance("SHA-384");
		byte[] messageDigest = md.digest(text.getBytes());

		StringBuilder hash = new StringBuilder();
		for (byte b : messageDigest) {
			hash.append(String.format("%02X", b));
		}
		return hash.toString();
	}
}