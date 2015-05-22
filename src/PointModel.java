/**
 * Created by czerudla on 22.5.15.
 */
public class PointModel {
    private int id;
    private double x;
    private double y;
    private String label;

    public PointModel(int id, double x, double y, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getLabel() {
        return label;
    }
}
