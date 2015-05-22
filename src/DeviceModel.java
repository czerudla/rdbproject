/**
 * Created by czerudla on 22.5.15.
 */
public class DeviceModel {
    private String deviceId;
    private String deviceType;

    public DeviceModel(String deviceId, String deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }
}
