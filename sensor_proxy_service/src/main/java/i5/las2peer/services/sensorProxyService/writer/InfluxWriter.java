package i5.las2peer.services.sensorProxyService.writer;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.services.sensorProxyService.pojo.context.ContextData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.mood.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.sensor.bitalino.BitalinoData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.bitalino.BitalinoMeasurement;
import i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric.MoodmetricData;
import i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric.MoodmetricMeasurement;
import i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric.RawMeasurement;

import java.util.ArrayList;
import java.util.List;

public class InfluxWriter {
    private final L2pLogger logger = L2pLogger.getInstance(InfluxWriter.class.getName());

    private static final String INFLUXDB_HOST = System.getenv("INFLUXDB_HOST");
    private static final char[] INFLUXDB_TOKEN = System.getenv("INFLUXDB_TOKEN").toCharArray();
    private static final String INFLUXDB_ORG = System.getenv("INFLUXDB_ORG");
    private static final String INFLUXDB_BUCKET = System.getenv("INFLUXDB_BUCKET");

    private final String userHash;

    private InfluxDBClient influxDBClient;
    private WriteApiBlocking writeApi;

    public InfluxWriter(String userHash) {
        this.influxDBClient = InfluxDBClientFactory.create(
                INFLUXDB_HOST,
                INFLUXDB_TOKEN,
                INFLUXDB_ORG,
                INFLUXDB_BUCKET);
        this.writeApi = this.influxDBClient.getWriteApiBlocking();
        this.userHash = userHash;
    }

    public void close() {
        logger.info("Closing connection..");
        this.influxDBClient.close();
    }

    public void writeMoodmetric(MoodmetricData moodmetricData) {
        List<Point> dataPoints = new ArrayList<>();
        logger.info("Collect data...");
        for (MoodmetricMeasurement moodmetricMeasurement : moodmetricData.getMoodmetricMeasurements()) {
            Long timestamp = moodmetricMeasurement.getCreatedAt();
            Point mmPoint = Point.measurement(this.userHash)
                    .addTag("studyID", moodmetricData.getStudyId())
                    .addField("instant", moodmetricMeasurement.getInstant())
                    .addField("acceleration", moodmetricMeasurement.getAcceleration())
                    .addField("moodmetric-level", moodmetricMeasurement.getLevel())
                    .time(timestamp, WritePrecision.MS);
            dataPoints.add(mmPoint);
        }

        for (MoodEvaluation moodEvaluation : moodmetricData.getMoodEvaluations()) {
            Point evalPoint = Point.measurement(this.userHash)
                    .addTag("studyID", moodmetricData.getStudyId())
                    .addField("estimated_valence", moodEvaluation.getValence())
                    .addField("estimated_arousal", moodEvaluation.getArousal())
                    .addField("approximated_mood", moodEvaluation.getMood().getName())
                    .addField("approximated_mood_valence", moodEvaluation.getMood().getValence())
                    .addField("approximated_mood_arousal", moodEvaluation.getMood().getArousal())
                    .addField("learning_relation", moodEvaluation.getLearningRelation())
                    .addField("problem_awareness", moodEvaluation.getHasProblemAwareness())
                    .addField("awareness_description", moodEvaluation.getAdditionalAwarenessDescription())
                    .addField("intervention_request", moodEvaluation.getHasInterventionRequest())
                    .addField("intervention_description", moodEvaluation.getAdditionalInterventionDescription())
                    .time(moodEvaluation.getCreatedAt(), WritePrecision.MS);
            dataPoints.add(evalPoint);
        }

        for (RawMeasurement rawMeasurement : moodmetricData.getRawSkinResistanceMeasurements()) {
            Point rawPoint = Point.measurement(this.userHash)
                    .addTag("studyID", moodmetricData.getStudyId())
                    .addField("raw", rawMeasurement.getRawSkinResistance())
                    .time(rawMeasurement.getCreatedAt(), WritePrecision.MS);
            dataPoints.add(rawPoint);
        }
        logger.info("Write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("Wrote " + dataPoints.size() + " entries");
    }

    public void writeBitalino(BitalinoData bitalinoData) {
        List<Point> dataPoints = new ArrayList<>();
        logger.info("Collect data...");
        for (BitalinoMeasurement bitalinoMeasurement : bitalinoData.getBitalinoMeasurement()) {
            Point bitalinoPoint = Point.measurement(this.userHash)
                    .addTag("studyID", bitalinoData.getStudyId())
                    .addField("s1", bitalinoMeasurement.getS1())
                    .addField("s2", bitalinoMeasurement.getS2())
                    .addField("s3", bitalinoMeasurement.getS3())
                    .addField("s4", bitalinoMeasurement.getS4())
                    .addField("s5", bitalinoMeasurement.getS5())
                    .addField("s6", bitalinoMeasurement.getS6())
                    .time(bitalinoMeasurement.getT(), WritePrecision.MS);
            dataPoints.add(bitalinoPoint);
        }

        logger.info("Write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("Wrote " + dataPoints.size() + " entries");
    }

    public void writeContext(ContextData contextData) {
        Point contextPoint = Point.measurement(this.userHash)
                .addTag("studyID", contextData.getStudyId())
                .addField("collaboration", contextData.getCollaboration())
                .addField("environment", contextData.getEnvironment())
                .addField("modality", contextData.getModality())
                .time(contextData.getCreatedAt(), WritePrecision.MS);

        logger.info("Write context-data to db...");
        this.writeApi.writePoint(contextPoint);
    }
}
