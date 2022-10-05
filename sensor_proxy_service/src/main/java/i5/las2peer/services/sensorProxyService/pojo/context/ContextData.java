package i5.las2peer.services.sensorProxyService.pojo.context;

public class ContextData {
    private final Long createdAt;
    private final String environment;
    private final String collaboration;
    private final String modality;

    public ContextData(Long createdAt, String environment, String collaboration, String modality) {
        this.createdAt = createdAt;
        this.environment = environment;
        this.collaboration = collaboration;
        this.modality = modality;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getCollaboration() {
        return collaboration;
    }

    public String getModality() {
        return modality;
    }
}
