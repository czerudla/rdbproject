/**
 * Created by czerudla on 22.5.15.
 */
public class SearchResultModel {
    private int date;
    private double val1;
    private double val2;
    private double valDiff;
    private String device;
    private double accuracy;

    public SearchResultModel(int date, double val1, double val2, double valDiff, String device, double accuracy) {
        this.date = date;
        this.val1 = val1;
        this.val2 = val2;
        this.valDiff = valDiff;
        this.device = device;
        this.accuracy = accuracy;
    }

    public int getDate() {
        return date;
    }

    public double getVal1() {
        return val1;
    }

    public double getVal2() {
        return val2;
    }

    public double getValDiff() {
        return valDiff;
    }

    public String getDevice() {
        return device;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
