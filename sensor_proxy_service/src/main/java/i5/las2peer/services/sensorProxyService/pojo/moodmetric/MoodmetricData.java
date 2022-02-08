package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

import i5.las2peer.services.sensorProxyService.pojo.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.SensorData;

import java.util.List;

public class MoodmetricData extends SensorData {
    private List<MoodmetricMeasurement> moodmetricMeasurement;
    private List<RawMeasurement> rawMeasurement;
    private AggregatedArrayData aggregatedArrayData;

    public MoodmetricData(String userID, List<MoodmetricMeasurement> moodmetricMeasurement, List<MoodEvaluation> moodEvaluation, List<RawMeasurement> rawMeasurement, AggregatedArrayData aggregatedArrayData) {
        this.userID = userID;
        this.moodmetricMeasurement = moodmetricMeasurement;
        this.moodEvaluation = moodEvaluation;
        this.rawMeasurement = rawMeasurement;
        this.aggregatedArrayData = aggregatedArrayData;
    }

    public List<MoodmetricMeasurement> getMoodmetricMeasurement() {
        return moodmetricMeasurement;
    }

    public void setMoodmetricMeasurement(List<MoodmetricMeasurement> moodmetricMeasurement) {
        this.moodmetricMeasurement = moodmetricMeasurement;
    }

    public List<RawMeasurement> getRawMeasurement() {
        return rawMeasurement;
    }

    public void setRawMeasurement(List<RawMeasurement> rawMeasurement) {
        this.rawMeasurement = rawMeasurement;
    }

    public AggregatedArrayData getAggregatedArrayData() {
        return aggregatedArrayData;
    }

    public void setAggregatedArrayData(AggregatedArrayData aggregatedArrayData) {
        this.aggregatedArrayData = aggregatedArrayData;
    }
}

