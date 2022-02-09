package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class AggregatedArrayData {
    private Double mv;
    private Double i;
    private Double a;
    private Double r;
    private String mk;

    public AggregatedArrayData(Double mv, Double i, Double a, Double r, String mk) {
        this.mv = mv;
        this.i = i;
        this.a = a;
        this.r = r;
        this.mk = mk;
    }

    public Double getMv() {
        return mv;
    }

    public void setMv(Double mv) {
        this.mv = mv;
    }

    public Double getI() {
        return i;
    }

    public void setI(Double i) {
        this.i = i;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public String getMk() {
        return mk;
    }

    public void setMk(String mk) {
        this.mk = mk;
    }
}
