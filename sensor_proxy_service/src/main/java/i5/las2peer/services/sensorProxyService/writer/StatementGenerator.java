package i5.las2peer.services.sensorProxyService.writer;

import i5.las2peer.logging.L2pLogger;
import i5.las2peer.services.sensorProxyService.pojo.sensor.mood.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.sensor.SensorData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric.MoodmetricData;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class StatementGenerator {

	private final String TECH4COMP_URI = "https://tech4comp.de/xapi";
	private final L2pLogger log = L2pLogger.getInstance(StatementGenerator.class.getName());

	public JSONObject createStatementFromAppData(SensorData sensorData, String userMail) {
		JSONObject retStatement = new JSONObject();

		// Add actor
		try {
			JSONObject actorJSON =  createActor(userMail);
			retStatement.put("actor", actorJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the actor data");
			return null;
		}

		//Add verb
		try {
			JSONObject verbJSON = (sensorData instanceof MoodmetricData) ? createVerb("evaluated_in") : createVerb("evaluated_bitalino_in");
			retStatement.put("verb", verbJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the verb data");
			return null;
		}

		//Add object
		try {
			JSONObject objectJSON = createObject(sensorData.getStudyId());
			retStatement.put("object", objectJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the object data");
			return null;
		}

		//Add context extensions
		try {
			JSONObject contextJSON = createContextExtensions(sensorData);
			retStatement.put("context", contextJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the context extension data");
			return null;
		}
		return retStatement;
	}

	private JSONObject createActor(String userEmail) {
		JSONObject actorJSON = new JSONObject();

		actorJSON.put("objectType", "Agent");
		JSONObject account = new JSONObject();
		account.put("name", userEmail);

		account.put("homePage", "https://www.tech4comp.dbis.rwth-aachen.de/");
		actorJSON.put("account", account);

		return actorJSON;
	}

	private JSONObject createVerb(String verb) {
		JSONObject verbJSON = new JSONObject();

		String id = TECH4COMP_URI + "/verbs/" + verb;
		verbJSON.put("id", id);

		JSONObject displayJSON = new JSONObject();
		displayJSON.put("en-US", verb);
		verbJSON.put("display", displayJSON);

		return verbJSON;
	}

	private JSONObject createObject(String studyID) throws JSONException {
		JSONObject objectJSON = new JSONObject();

		String id = TECH4COMP_URI + "/activities/study_" + studyID;
		objectJSON.put("id", id);

		String objectType = "Activity";
		objectJSON.put("objectType", objectType);

		JSONObject definitionJSON = new JSONObject();
		JSONObject nameJSON = new JSONObject();
		nameJSON.put("en-US", studyID);
		JSONObject descriptionJSON = new JSONObject();
		descriptionJSON.put("en-US", "A tech4comp Moodmetric study evaluation");
		definitionJSON.put("name", nameJSON);
		definitionJSON.put("description", descriptionJSON);
		objectJSON.put("definition", definitionJSON);

		return objectJSON;
	}

	private JSONObject createContextExtensions(SensorData sensorData) throws JSONException {
		JSONObject contextJSON = new JSONObject();
		JSONObject extensionsJSON = new JSONObject();

		String keyCommonPart = TECH4COMP_URI + "/context/extensions";

		String moodEvalKey = keyCommonPart + "/moodEvaluation";
		List<MoodEvaluation> moodEvalVal = sensorData.getMoodEvaluations();

		extensionsJSON.put(moodEvalKey, moodEvalVal);

		contextJSON.put("extensions", extensionsJSON);
		return contextJSON;
	}
}
