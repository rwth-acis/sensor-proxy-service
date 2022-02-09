package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class MoodmetricMeasurement {
    private final Integer mv;
    private final Integer i;
    private final Double a;
    private final Long t;

    public MoodmetricMeasurement(Integer mv, Integer i, Double a, Long t) {
        this.mv = mv;
        this.i = i;
        this.a = a;
        this.t = t;
    }

    public Integer getMv() {
        return mv;
    }

    public Integer getI() {
        return i;
    }

    public Double getA() {
        return a;
    }

    public Long getT() {
        return t;
    }
}