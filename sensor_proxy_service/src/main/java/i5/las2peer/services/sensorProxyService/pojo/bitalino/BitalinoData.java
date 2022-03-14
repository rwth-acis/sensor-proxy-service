package i5.las2peer.services.sensorProxyService.pojo.bitalino;

import i5.las2peer.services.sensorProxyService.pojo.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.SensorData;

import java.util.List;

public class BitalinoData extends SensorData {
    private final List<BitalinoMeasurement> bitalinoMeasurement;

    public BitalinoData(String userID, List<BitalinoMeasurement> bitalinoMeasurement, List<MoodEvaluation> moodEvaluation) {
        this.userID = userID;
        this.bitalinoMeasurement = bitalinoMeasurement;
        this.moodEvaluation = moodEvaluation;
    }

    public List<BitalinoMeasurement> getBitalinoMeasurement() {
        return bitalinoMeasurement;
    }
}
