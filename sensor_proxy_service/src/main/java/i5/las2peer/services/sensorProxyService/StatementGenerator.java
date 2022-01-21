package i5.las2peer.services.sensorProxyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import i5.las2peer.logging.L2pLogger;

public class StatementGenerator {

	private final String TECH4COMP_URI = "https://tech4comp.de/xapi";
	private final L2pLogger log = L2pLogger.getInstance(StatementGenerator.class.getName());

	public JSONObject createStatementFromAppData(JSONObject dataJSON) {
		JSONObject retStatement = new JSONObject();

		// Add actor
		try {
			JSONObject actorJSON =  createActor(dataJSON.getString("userID"));
			retStatement.put("actor", actorJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the actor data");
			return null;
		}

		//Add verb
		try {
			JSONObject verbJSON = (isMoodmetric(dataJSON)) ? createVerb("evaluated") : createVerb("evaluated_bitalino");
			retStatement.put("verb", verbJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the verb data");
			return null;
		}

		//Add object
		try {
			JSONObject objectJSON = createObject(dataJSON.getString("studyID"));
			retStatement.put("object", objectJSON);
		} catch (JSONException e) {
			log.severe("There was a problem parsing the object data");
			return null;
		}

		//Add context extensions
		try {
			JSONObject contextJSON = createContextExtensions(dataJSON);
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

		String mboxValue = "mailto:" + userEmail;
		actorJSON.put("mbox", mboxValue);

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
		nameJSON.put("en-US", "Study_" + studyID);
		JSONObject descriptionJSON = new JSONObject();
		descriptionJSON.put("en-US", "A tech4comp Moodmetric study evaluation");
		definitionJSON.put("name", nameJSON);
		definitionJSON.put("description", descriptionJSON);
		objectJSON.put("definition", definitionJSON);

		return objectJSON;
	}

	private JSONObject createContextExtensions(JSONObject dataJSON) throws JSONException {
		JSONObject contextJSON = new JSONObject();
		JSONObject extensionsJSON = new JSONObject();

		String keyCommonPart = TECH4COMP_URI + "/context/extensions";

		String moodEvalKey = keyCommonPart + "/moodEvaluation";
		JSONArray moodEvalVal = dataJSON.getJSONArray("moodEvaluation");
		extensionsJSON.put(moodEvalKey, moodEvalVal);

		if (isMoodmetric(dataJSON)) {
			String moodmetricKey = keyCommonPart + "/moodmetricMeasurement";
			JSONArray moodmetricVal = dataJSON.getJSONArray("moodmetricMeasurement");
			extensionsJSON.put(moodmetricKey, moodmetricVal);

			String rawKey = keyCommonPart + "/rawMeasurement";
			JSONArray rawVal = dataJSON.getJSONArray("rawMeasurement");
			extensionsJSON.put(rawKey, rawVal);

			String avgKey = keyCommonPart + "/aggregatedArrayData";
			JSONObject avgVal = dataJSON.getJSONObject("aggregatedArrayData");
			extensionsJSON.put(avgKey, avgVal);
		} else {
			String bitalinoKey = keyCommonPart + "/bitalinoMeasurement";
			JSONArray bitalinoVal = dataJSON.getJSONArray("bitalinoMeasurement");
			extensionsJSON.put(bitalinoKey, bitalinoVal);
		}

		contextJSON.put("extensions", extensionsJSON);
		return contextJSON;
	}

	private boolean isMoodmetric(JSONObject dataJSON) {
		return dataJSON.has("rawMeasurement") && !dataJSON.getJSONArray("rawMeasurement").isEmpty();
	}
}
