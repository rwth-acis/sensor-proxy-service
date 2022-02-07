package i5.las2peer.services.sensorProxyService;

import com.google.gson.Gson;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.services.sensorProxyService.pojo.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.bitalino.BitalinoData;
import i5.las2peer.services.sensorProxyService.pojo.bitalino.BitalinoMeasurement;
import i5.las2peer.services.sensorProxyService.pojo.moodmetric.MoodmetricData;
import i5.las2peer.services.sensorProxyService.pojo.moodmetric.MoodmetricMeasurement;
import i5.las2peer.services.sensorProxyService.pojo.moodmetric.RawMeasurement;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfluxWriter {
    private final L2pLogger logger = L2pLogger.getInstance(InfluxWriter.class.getName());

    private static final String INFLUXDB_HOST = System.getenv("INFLUXDB_HOST");
    private static final char[] INFLUXDB_TOKEN = System.getenv("INFLUXDB_TOKEN").toCharArray();
    private static final String INFLUXDB_ORG = System.getenv("INFLUXDB_ORG");
    private static final String INFLUXDB_BUCKET = System.getenv("INFLUXDB_BUCKET");

    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;

    public InfluxWriter() {
        try {
            this.influxDBClient = InfluxDBClientFactory.create(
                    INFLUXDB_HOST,
                    INFLUXDB_TOKEN,
                    INFLUXDB_ORG,
                    INFLUXDB_BUCKET);
            this.writeApi = this.influxDBClient.getWriteApiBlocking();
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    public void close() {
        logger.info("close connection");
        this.influxDBClient.close();
    }

    public void writeData(JSONObject payload) {
        try {
            if (isMoodmetric(payload)) {
                logger.info("received moodmetric data");
                Gson gson = new Gson();
                MoodmetricData moodmetricData = gson.fromJson(payload.toString(), MoodmetricData.class);
                this.handleMoodmetric(moodmetricData);
            } else {
                logger.info("received bitalino data");
                Gson gson = new Gson();
                BitalinoData bitalinoData = gson.fromJson(payload.toString(), BitalinoData.class);
                this.handleBitalino(bitalinoData);
            }
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    private void handleMoodmetric(MoodmetricData moodmetricData) {
        String userID = this.getIDfromMail(moodmetricData.getUserID());

        List<Point> dataPoints = new ArrayList<>();
        logger.info("collect data...");
        for (MoodmetricMeasurement moodmetricMeasurement : moodmetricData.getMoodmetricMeasurement()) {
            Long timestamp = moodmetricMeasurement.getT();
            Point mmPoint = Point.measurement(userID)
                    .addField("instant", moodmetricMeasurement.getI())
                    .addField("acceleration", moodmetricMeasurement.getA())
                    .addField("moodmetric-score", moodmetricMeasurement.getMv())
                    .time(timestamp, WritePrecision.MS);
            dataPoints.add(mmPoint);
        }

        for (MoodEvaluation moodEvaluation : moodmetricData.getMoodEvaluation()) {
            Point evalPoint = Point.measurement(userID)
                    .addField("eval", moodEvaluation.getMk())
                    .time(moodEvaluation.getT(), WritePrecision.MS);
            dataPoints.add(evalPoint);
        }

        for (RawMeasurement rawMeasurement : moodmetricData.getRawMeasurement()) {
            Point rawPoint = Point.measurement(userID)
                    .addField("raw", rawMeasurement.getR())
                    .time(rawMeasurement.getT(), WritePrecision.MS);
            dataPoints.add(rawPoint);
        }

        logger.info("write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("wrote " + dataPoints.size() + " entries");
    }

    private void handleBitalino(BitalinoData bitalinoData) {
        String userID = this.getIDfromMail(bitalinoData.getUserID());

        List<Point> dataPoints = new ArrayList<>();
        logger.info("collect data...");
        for (BitalinoMeasurement bitalinoMeasurement : bitalinoData.getBitalinoMeasurement()) {
            Point bitalinoPoint = Point.measurement(userID)
                    .addField("s1", bitalinoMeasurement.getS1())
                    .addField("s2", bitalinoMeasurement.getS2())
                    .addField("s3", bitalinoMeasurement.getS3())
                    .addField("s4", bitalinoMeasurement.getS4())
                    .addField("s5", bitalinoMeasurement.getS5())
                    .addField("s6", bitalinoMeasurement.getS6())
                    .time(bitalinoMeasurement.getT(), WritePrecision.MS);
            dataPoints.add(bitalinoPoint);
        }

        logger.info("write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("wrote " + dataPoints.size() + " entries");
    }

    private String getIDfromMail(String mail) {
        return mail.split("@")[0];
    }

    private boolean isMoodmetric(JSONObject dataJSON) {
        return dataJSON.has("rawMeasurement") && !dataJSON.getJSONArray("rawMeasurement").isEmpty();
    }
}
