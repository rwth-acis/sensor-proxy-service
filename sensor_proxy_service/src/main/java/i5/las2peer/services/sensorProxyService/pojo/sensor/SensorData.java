package i5.las2peer.services.sensorProxyService.pojo.sensor;

import i5.las2peer.services.sensorProxyService.pojo.sensor.mood.MoodEvaluation;

import java.util.Collections;
import java.util.List;

public abstract class SensorData {

    protected Integer version;
    protected String studyID;
    protected List<MoodEvaluation> moodEvaluations;

    public List<MoodEvaluation> getMoodEvaluations() {
        return moodEvaluations == null ? Collections.<MoodEvaluation>emptyList() : moodEvaluations;
    }

    public String getStudyID() {
        return studyID;
    }
}
