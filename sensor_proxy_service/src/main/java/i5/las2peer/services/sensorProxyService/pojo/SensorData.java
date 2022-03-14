package i5.las2peer.services.sensorProxyService.pojo;

import java.util.List;

public abstract class SensorData {
    protected String userID;
    protected String studyID;
    protected List<MoodEvaluation> moodEvaluation;

    public String getUserID() {
        return userID;
    }

    public List<MoodEvaluation> getMoodEvaluation() {
        return moodEvaluation;
    }

    public String getStudyID() {
        return studyID;
    }
}
