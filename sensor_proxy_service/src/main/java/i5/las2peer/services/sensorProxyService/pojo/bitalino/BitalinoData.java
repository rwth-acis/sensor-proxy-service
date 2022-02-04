package i5.las2peer.services.sensorProxyService.pojo.bitalino;

import java.util.List;

public class BitalinoData {
    private String userID;
    private List<BitalinoMeasurement> bitalinoMeasurement;

    public BitalinoData(String userID, List<BitalinoMeasurement> bitalinoMeasurement) {
        this.userID = userID;
        this.bitalinoMeasurement = bitalinoMeasurement;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<BitalinoMeasurement> getBitalinoMeasurement() {
        return bitalinoMeasurement;
    }

    public void setBitalinoMeasurement(List<BitalinoMeasurement> bitalinoMeasurement) {
        this.bitalinoMeasurement = bitalinoMeasurement;
    }
}
