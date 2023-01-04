package i5.las2peer.services.sensorProxyService.pojo.sensor.mood;

public class MoodEvaluation {
    private final Double valence;
    private final Double arousal;
    private final Long createdAt;
    private final Mood mood;

    private final Integer learningRelation;
    private final Boolean hasProblemAwareness;
    private final String additionalAwarenessDescription;
    private final Boolean hasInterventionRequest;
    private final String additionalInterventionDescription;

    public MoodEvaluation(Double valence, Double arousal, Long createdAt, Mood mood, Integer learningRelation, Boolean hasProblemAwareness, String additionalAwarenessDescription, Boolean hasInterventionDescription, String additionalInterventionDescription) {
        this.valence = valence;
        this.arousal = arousal;
        this.createdAt = createdAt;
        this.mood = mood;
        this.learningRelation = learningRelation;
        this.hasProblemAwareness = hasProblemAwareness;
        this.additionalAwarenessDescription = additionalAwarenessDescription;
        this.hasInterventionRequest = hasInterventionDescription;
        this.additionalInterventionDescription = additionalInterventionDescription;
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

    public Integer getLearningRelation() {
        return learningRelation;
    }

    public Boolean getHasProblemAwareness() {
        return hasProblemAwareness;
    }

    public String getAdditionalAwarenessDescription() {
        return additionalAwarenessDescription;
    }

    public Boolean getHasInterventionRequest() {
        return hasInterventionRequest;
    }

    public String getAdditionalInterventionDescription() {
        return additionalInterventionDescription;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}

