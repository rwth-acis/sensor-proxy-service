package i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric;

public class MoodmetricMeasurement {
    private final Integer level;
    private final Integer instant;
    private final Double acceleration;
    private final Long createdAt;

    public MoodmetricMeasurement(Integer level, Integer instant, Double acceleration, Long createdAt) {
        this.level = level;
        this.instant = instant;
        this.acceleration = acceleration;
        this.createdAt = createdAt;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getInstant() {
        return instant;
    }

    public Double getAcceleration() {
        return acceleration;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}