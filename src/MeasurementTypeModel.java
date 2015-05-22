/**
 * Created by czerudla on 22.5.15.
 */
public class MeasurementTypeModel {
    private int measurementId;
    private String measurementDesc;

    public MeasurementTypeModel(int measurementId, String measurementDesc) {
        this.measurementId = measurementId;
        this.measurementDesc = measurementDesc;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public String getMeasurementDesc() {
        return measurementDesc;
    }
}
