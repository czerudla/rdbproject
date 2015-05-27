import java.io.*;

public class RdbMainDriver {

    public static void main(String[] args) throws IOException {

        //DbFunctions.copyDataToDatabase("src/test.csv1431527571.txt");

        FrontEnd f = new FrontEnd();

        //System.out.println(DbFunctions.timestampToDate(1432679265));
        //System.out.println(DbFunctions.timestampToFullDate(1432679265));

        /*
        DbFunctions.selectByDate("2015-05-13", "2015-05-13");
        DbFunctions.getPoints();
        DbFunctions.getDevices();
        DbFunctions.getMeasurementTypes();
        DbFunctions.getUnits();

        DbFunctions.deleteSelectedDevice("AMPER METER 1");
        DbFunctions.deleteSelectedMeasurement("Measurement desc 4");

        DbFunctions.exportResult(DbFunctions.selectByDate("2015-05-13", "2015-05-13"), "Output.txt");
        */

    }

}
