package i5.las2peer.services.sensorProxyService.pojo.sensor.mood;

public class Mood {
    private final String name;
    private final Double valence;
    private final Double arousal;

    public Mood(String name, Double valence, Double arousal) {
        this.name = name;
        this.valence = valence;
        this.arousal = arousal;
    }

    public String getName() {
        return name;
    }

    public Double getValence() {
        return valence;
    }

    public Double getArousal() {
        return arousal;
    }
}
