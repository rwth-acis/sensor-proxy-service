package i5.las2peer.services.sensorProxyService.pojo;

public class MoodEvaluation {
    private final String mk;
    private final Long t;

    public MoodEvaluation(String mk, Long t) {
        this.mk = mk;
        this.t = t;
    }

    public String getMk() {
        return mk;
    }

    public Long getT() {
        return t;
    }
}
