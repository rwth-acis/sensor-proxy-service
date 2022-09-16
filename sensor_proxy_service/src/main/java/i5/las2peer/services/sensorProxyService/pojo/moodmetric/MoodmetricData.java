package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

import i5.las2peer.services.sensorProxyService.pojo.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.SensorData;

import java.util.Collections;
import java.util.List;

public class MoodmetricData extends SensorData {
    private final List<MoodmetricMeasurement> moodmetricMeasurement;
    private final List<RawMeasurement> rawMeasurement;
    private final AggregatedArrayData aggregatedArrayData;

    public MoodmetricData(String userID, List<MoodmetricMeasurement> moodmetricMeasurement, List<MoodEvaluation> moodEvaluation, List<RawMeasurement> rawMeasurement, AggregatedArrayData aggregatedArrayData) {
        this.userID = userID;
        this.moodmetricMeasurement = moodmetricMeasurement;
        this.moodEvaluation = moodEvaluation;
        this.rawMeasurement = rawMeasurement;
        this.aggregatedArrayData = aggregatedArrayData;
    }

    public List<MoodmetricMeasurement> getMoodmetricMeasurement() {
        return moodmetricMeasurement == null ? Collections.<MoodmetricMeasurement>emptyList() : moodmetricMeasurement;
    }

    public List<RawMeasurement> getRawMeasurement() {
        return rawMeasurement == null ? Collections.<RawMeasurement>emptyList() : rawMeasurement;
    }

    public AggregatedArrayData getAggregatedArrayData() {
        return aggregatedArrayData;
    }
}

