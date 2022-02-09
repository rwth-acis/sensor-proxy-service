package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class RawMeasurement {
    private final Integer r;
    private final Long t;

    public RawMeasurement(Integer r, Long t) {
        this.r = r;
        this.t = t;
    }

    public Integer getR() {
        return r;
    }

    public Long getT() {
        return t;
    }
}
