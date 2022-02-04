package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class RawMeasurement {
    private Integer r;
    private long t;

    public RawMeasurement(Integer r, long t) {
        this.r = r;
        this.t = t;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }
}
