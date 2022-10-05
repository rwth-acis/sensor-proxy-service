package i5.las2peer.services.sensorProxyService.pojo.sensor.bitalino;

import i5.las2peer.services.sensorProxyService.pojo.sensor.mood.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.sensor.SensorData;

import java.util.Collections;
import java.util.List;

public class BitalinoData extends SensorData {
    private final List<BitalinoMeasurement> bitalinoMeasurement;

    public BitalinoData(List<BitalinoMeasurement> bitalinoMeasurement, List<MoodEvaluation> moodEvaluations) {
        this.bitalinoMeasurement = bitalinoMeasurement;
        this.moodEvaluations = moodEvaluations;
    }

    public List<BitalinoMeasurement> getBitalinoMeasurement() {
        return bitalinoMeasurement == null ? Collections.<BitalinoMeasurement>emptyList() : bitalinoMeasurement;
    }
}
