package i5.las2peer.services.sensorProxyService.writer;

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
        logger.info("Closing connection..");
        this.influxDBClient.close();
    }

    public void writeMoodmetric(MoodmetricData moodmetricData) {
        String userID = this.getIDfromMail(moodmetricData.getUserID());

        List<Point> dataPoints = new ArrayList<>();
        logger.info("Collect data...");
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

        logger.info("Write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("Wrote " + dataPoints.size() + " entries");
    }

    public void writeBitalino(BitalinoData bitalinoData) {
        String userID = this.getIDfromMail(bitalinoData.getUserID());

        List<Point> dataPoints = new ArrayList<>();
        logger.info("Collect data...");
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

        logger.info("Write data to db...");
        this.writeApi.writePoints(dataPoints);
        logger.info("Wrote " + dataPoints.size() + " entries");
    }

    private String getIDfromMail(String mail) {
        return mail.split("@")[0];
    }
}
