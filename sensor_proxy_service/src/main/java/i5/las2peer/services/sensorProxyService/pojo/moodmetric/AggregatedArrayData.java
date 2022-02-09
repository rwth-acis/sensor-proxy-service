package i5.las2peer.services.sensorProxyService.pojo.moodmetric;

public class AggregatedArrayData {
    private final Double mv;
    private final Double i;
    private final Double a;
    private final Double r;
    private final String mk;

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

    public Double getI() {
        return i;
    }

    public Double getA() {
        return a;
    }

    public Double getR() {
        return r;
    }

    public String getMk() {
        return mk;
    }
}
