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
import java.util.Collections;
import java.util.List;

public class InfluxWriter {
    private final L2pLogger logger = L2pLogger.getInstance(InfluxWriter.class.getName());

    private static final String INFLUXDB_HOST = "http://192.168.178.20:8086";
    private static final char[] INFLUXDB_TOKEN = "t_kz-_CH15C2oExHRGqMwlCTiHVbJCsgYxPF9i8QVVF_GQof-5dVzJIFhuY7rGRhmHCRk-w7fzqqDq6qFpQdCw==".toCharArray();
    private static final String INFLUXDB_ORG = "org";
    private static final String INFLUXDB_BUCKET = "bucket";

    private WriteApiBlocking writeApi;

    public InfluxWriter() {
        try {
            InfluxDBClient influxDBClient = InfluxDBClientFactory.create(
                    INFLUXDB_HOST,
                    INFLUXDB_TOKEN,
                    INFLUXDB_ORG,
                    INFLUXDB_BUCKET);
            this.writeApi = influxDBClient.getWriteApiBlocking();
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }
    public void writeData(JSONObject payload) {
        // TODO: create bucket if not exist
        // TODO: close connection
        // TODO: remove @tech4comp.com from studyID

        if (isMoodmetric(payload)) {
            logger.info("received moodmetric data");
            Gson gson = new Gson();
            MoodmetricData moodmetricData = gson.fromJson(payload.toString(), MoodmetricData.class);
            String userID = moodmetricData.getUserID();

            List<Point> dataPoints = new ArrayList<>();
            logger.info("collect moodmetricMeasurement data...");
            for (MoodmetricMeasurement moodmetricMeasurement : moodmetricData.getMoodmetricMeasurement()) {
                Long timestamp = moodmetricMeasurement.getT();
                Point instantPoint = Point.measurement(userID)
                        .addField("instant", moodmetricMeasurement.getI())
                        .time(timestamp, WritePrecision.MS);

                Point accelerationPoint = Point.measurement(userID)
                        .addField("acceleration", moodmetricMeasurement.getA())
                        .time(timestamp, WritePrecision.MS);
                Point moodmetricScorePoint = Point.measurement(userID)
                        .addField("moodmetric-score", moodmetricMeasurement.getMv())
                        .time(timestamp, WritePrecision.MS);
                Collections.addAll(dataPoints, instantPoint, accelerationPoint, moodmetricScorePoint);
            }

            logger.info("collect moodEvaluation data...");
            for (MoodEvaluation moodEvaluation : moodmetricData.getMoodEvaluation()) {
                Point evalPoint = Point.measurement(userID)
                        .addField("eval", moodEvaluation.getMk())
                        .time(moodEvaluation.getT(), WritePrecision.MS);
                dataPoints.add(evalPoint);
            }

            logger.info("collect rawMeasurement data...");
            for (RawMeasurement rawMeasurement : moodmetricData.getRawMeasurement()) {
                Point rawPoint = Point.measurement(userID)
                        .addField("raw", rawMeasurement.getR())
                        .time(rawMeasurement.getT(), WritePrecision.MS);
                dataPoints.add(rawPoint);
            }

            logger.info("write data to db...");
            this.writeApi.writePoints(dataPoints);
            logger.info("wrote " + dataPoints.size() + " entries");
        } else {
            logger.info("received bitalino data");
            Gson gson = new Gson();
            BitalinoData bitalinoData = gson.fromJson(payload.toString(), BitalinoData.class);
            String userID = bitalinoData.getUserID();

            List<Point> dataPoints = new ArrayList<>();
            logger.info("collect bitalinoMeasurement data...");
            for (BitalinoMeasurement bitalinoMeasurement : bitalinoData.getBitalinoMeasurement()) {
                Long timestamp = bitalinoMeasurement.getT();

                Integer s1 = bitalinoMeasurement.getS1();
                Integer s2 = bitalinoMeasurement.getS2();
                Integer s3 = bitalinoMeasurement.getS3();
                Integer s4 = bitalinoMeasurement.getS4();
                Integer s5 = bitalinoMeasurement.getS5();
                Integer s6 = bitalinoMeasurement.getS6();

                // it's possible that s1 - s6 is null if sensor isn't connected -> do null check
                if (s1 != null) {
                    Point s1Point = Point.measurement(userID)
                            .addField("s1", s1)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s1Point);
                }
                if (s2 != null) {
                    Point s2Point = Point.measurement(userID)
                            .addField("s2", s2)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s2Point);
                }
                if (s3 != null) {
                    Point s3Point = Point.measurement(userID)
                            .addField("s3", s3)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s3Point);
                }
                if (s4 != null) {
                    Point s4Point = Point.measurement(userID)
                            .addField("s4", s4)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s4Point);
                }
                if (s5 != null) {
                    Point s5Point = Point.measurement(userID)
                            .addField("s5", s5)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s5Point);
                }
                if (s6 != null) {
                    Point s6Point = Point.measurement(userID)
                            .addField("s6", s6)
                            .time(timestamp, WritePrecision.MS);
                    dataPoints.add(s6Point);
                }
            }

            logger.info("write data to db...");
            this.writeApi.writePoints(dataPoints);
            logger.info("wrote " + dataPoints.size() + " entries");
        }
    }

    private boolean isMoodmetric(JSONObject dataJSON) {
        return dataJSON.has("rawMeasurement") && !dataJSON.getJSONArray("rawMeasurement").isEmpty();
    }
}
