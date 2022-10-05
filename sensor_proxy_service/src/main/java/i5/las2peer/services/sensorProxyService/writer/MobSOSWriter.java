package i5.las2peer.services.sensorProxyService.writer;

import i5.las2peer.api.Context;
import i5.las2peer.api.logging.MonitoringEvent;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.services.sensorProxyService.pojo.sensor.SensorData;
import org.json.JSONArray;
import org.json.JSONObject;

public class MobSOSWriter {
    private final L2pLogger logger = L2pLogger.getInstance(MobSOSWriter.class.getName());
    private final StatementGenerator generator = new StatementGenerator();

    public void write(SensorData sensorData, JSONObject rawPayload, String userMail) {
        // create xapi statement
        JSONObject statement = generator.createStatementFromAppData(sensorData, userMail);

        JSONObject msg = new JSONObject();
        msg.put("statement", statement);
        JSONArray tokens = new JSONArray();
        tokens.put(userMail);
        msg.put("tokens", tokens);

        String eventMessage = msg.toString();
        logger.info("Created statement: " + statement.toString());
        logger.info("Forwarding statement to MobSOS with token: " + userMail);

        Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_1, eventMessage);
        Context.get().monitorEvent(MonitoringEvent.SERVICE_CUSTOM_MESSAGE_2, rawPayload.toString());
    }
}
