import java.io.*;

public class RdbMainDriver {

    public static void main(String[] args) throws IOException {

        DbFunctions.copyDataToDatabase("src/test.csv1431527571.txt");

        FrontEnd f = new FrontEnd();

        DbFunctions.selectByDate("2015-05-13", "2015-05-13");
        DbFunctions.getPoints();
        DbFunctions.getDevices();
        DbFunctions.getMeasurementTypes();
        DbFunctions.getUnits();

        DbFunctions.deleteSelectedDevice("AMPER METER 1");
        DbFunctions.deleteSelectedMeasurement("Measurement desc 4");
    }

}
