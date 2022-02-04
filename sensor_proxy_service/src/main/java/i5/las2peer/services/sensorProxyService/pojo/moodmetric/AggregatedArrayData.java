package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class AggregatedArrayData {
    private Integer mv;
    private Integer i;
    private Integer a;
    private Integer r;
    private String mk;

    public AggregatedArrayData(Integer mv, Integer i, Integer a, Integer r, String mk) {
        this.mv = mv;
        this.i = i;
        this.a = a;
        this.r = r;
        this.mk = mk;
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

    public Integer getA() {
        return a;
    }

    public void setA(Integer a) {
        this.a = a;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public String getMk() {
        return mk;
    }

    public void setMk(String mk) {
        this.mk = mk;
    }
}
