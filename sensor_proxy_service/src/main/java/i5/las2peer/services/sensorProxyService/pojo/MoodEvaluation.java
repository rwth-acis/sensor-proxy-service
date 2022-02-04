package i5.las2peer.services.sensorProxyService.pojo;

public class MoodEvaluation {
    private String mk;
    private long t;

    public MoodEvaluation(String mk, long t) {
        this.mk = mk;
        this.t = t;
    }

    public String getMk() {
        return mk;
    }

    public void setMk(String mk) {
        this.mk = mk;
    }

    public long getT() {
        return t;
    }

    public void setT(long t) {
        this.t = t;
    }
}
