package i5.las2peer.services.sensorProxyService.pojo.sensor.moodmetric;

public class RawMeasurement {
    private final Integer rawSkinResistance;
    private final Long createdAt;

    public RawMeasurement(Integer rawSkinResistance, Long createdAt) {
        this.rawSkinResistance = rawSkinResistance;
        this.createdAt = createdAt;
    }

    public Integer getRawSkinResistance() {
        return rawSkinResistance;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}
