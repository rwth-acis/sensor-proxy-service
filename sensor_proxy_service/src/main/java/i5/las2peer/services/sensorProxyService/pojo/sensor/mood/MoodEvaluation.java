package i5.las2peer.services.sensorProxyService.pojo.sensor.mood;

public class MoodEvaluation {
    private final Double valence;
    private final Double arousal;
    private final Long createdAt;
    private final Mood mood;


    public MoodEvaluation(Double valence, Double arousal, Long createdAt, Mood mood) {
        this.valence = valence;
        this.arousal = arousal;
        this.createdAt = createdAt;
        this.mood = mood;
    }

    public Double getValence() {
        return valence;
    }

    public Double getArousal() {
        return arousal;
    }

    public Mood getMood() {
        return mood;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}

