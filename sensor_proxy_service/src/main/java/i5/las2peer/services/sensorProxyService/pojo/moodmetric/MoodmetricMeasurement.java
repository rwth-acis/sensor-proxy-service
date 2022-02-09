package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class MoodmetricMeasurement {
    private Integer mv;
    private Integer i;
    private Double a;
    private long t;

    public MoodmetricMeasurement(Integer mv, Integer i, Double a, long t) {
        this.mv = mv;
        this.i = i;
        this.a = a;
        this.t = t;
    }

    public Integer getMv() {
        return mv;
    }

    public void setMv(Integer mv) {
        this.mv = mv;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }
}