package i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric;

import i5.las2peer.services.sensorProxyService.pojo.sensor.mood.MoodEvaluation;
import i5.las2peer.services.sensorProxyService.pojo.sensor.SensorData;

import java.util.Collections;
import java.util.List;

public class MoodmetricData extends SensorData {
    private final List<MoodmetricMeasurement> moodmetricMeasurements;
    private final List<RawMeasurement> rawSkinResistanceMeasurements;

    public MoodmetricData(List<MoodmetricMeasurement> moodmetricMeasurements, List<MoodEvaluation> moodEvaluations, List<RawMeasurement> rawSkinResistanceMeasurements) {
        this.moodmetricMeasurements = moodmetricMeasurements;
        this.moodEvaluations = moodEvaluations;
        this.rawSkinResistanceMeasurements = rawSkinResistanceMeasurements;
    }

    public List<MoodmetricMeasurement> getMoodmetricMeasurements() {
        return moodmetricMeasurements == null ? Collections.<MoodmetricMeasurement>emptyList() : moodmetricMeasurements;
    }

    public List<RawMeasurement> getRawSkinResistanceMeasurements() {
        return rawSkinResistanceMeasurements == null ? Collections.<RawMeasurement>emptyList() : rawSkinResistanceMeasurements;
    }
}

