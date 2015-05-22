/**
 * Created by czerudla on 22.5.15.
 */
public class DeviceModel {
    private String deviceId;
    private String devicetype;

    public DeviceModel(String deviceId, String deviceModel) {
        this.deviceId = deviceId;
        this.devicetype = deviceModel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceModel() {
        return devicetype;
    }
}
