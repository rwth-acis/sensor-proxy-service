package i5.las2peer.services.sensorProxyService.pojo.sensor.bitalino;

public class BitalinoMeasurement {
    private final Long t;
    private final Integer s1;
    private final Integer s2;
    private final Integer s3;
    private final Integer s4;
    private final Integer s5;
    private final Integer s6;

    public BitalinoMeasurement(Long t, Integer s1, Integer s2, Integer s3, Integer s4, Integer s5, Integer s6) {
        this.t = t;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.s6 = s6;
    }

    public Long getT() {
        return t;
    }

    public Integer getS1() {
        return s1;
    }

    public Integer getS2() {
        return s2;
    }

    public Integer getS3() {
        return s3;
    }

    public Integer getS4() {
        return s4;
    }

    public Integer getS5() {
        return s5;
    }

    public Integer getS6() {
        return s6;
    }
}
