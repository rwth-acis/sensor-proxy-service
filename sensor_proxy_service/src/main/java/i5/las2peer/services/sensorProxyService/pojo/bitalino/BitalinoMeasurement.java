package i5.las2peer.services.sensorProxyService.pojo.bitalino;

public class BitalinoMeasurement {
    private long t;
    private Integer s1;
    private Integer s2;
    private Integer s3;
    private Integer s4;
    private Integer s5;
    private Integer s6;

    public BitalinoMeasurement(long t, Integer s1, Integer s2, Integer s3, Integer s4, Integer s5, Integer s6) {
        this.t = t;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.s6 = s6;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }

    public Integer getS1() {
        return s1;
    }

    public void setS1(Integer s1) {
        this.s1 = s1;
    }

    public Integer getS2() {
        return s2;
    }

    public void setS2(Integer s2) {
        this.s2 = s2;
    }

    public Integer getS3() {
        return s3;
    }

    public void setS3(Integer s3) {
        this.s3 = s3;
    }

    public Integer getS4() {
        return s4;
    }

    public void setS4(Integer s4) {
        this.s4 = s4;
    }

    public Integer getS5() {
        return s5;
    }

    public void setS5(Integer s5) {
        this.s5 = s5;
    }

    public Integer getS6() {
        return s6;
    }

    public void setS6(Integer s6) {
        this.s6 = s6;
    }
}
