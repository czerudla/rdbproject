import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by czerudla on 21.5.15.
 */
public class DbFunctions {
    private static String dbAdress = DbAccess.getDbAdress();
    private static String dbLogin = DbAccess.getDbLogin();
    private static String dbPass = DbAccess.getDbPass();

    private static Connection conn = null;
    private static Statement query = null;

    private static String exportStatement = null;

    public static void copyDataToDatabase(String filename) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            CopyManager copyManager = new CopyManager((BaseConnection) conn);
            FileReader fileReader = new FileReader(filename);
            copyManager.copyIn("COPY vstup FROM STDIN DELIMITER ';' CSV", fileReader);
            conn.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public static ArrayList<SearchResultModel> selectByAllValues(String dateFrom, String dateTo, String valDiff,
                                                                 String measurementDesc, String deviceType, String pointLabel, String unit) {
        ArrayList<SearchResultModel> results = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            String statement = getStatement(dateFrom, dateTo, valDiff, measurementDesc, deviceType, pointLabel, unit);

            System.out.println(statement);

            ResultSet rs = query.executeQuery(statement);
            while ( rs.next() ) {
                int date = rs.getInt("datum_cas");
                double val1 = rs.getDouble("hodnota_1");
                double val2 = rs.getDouble("hodnota_2");
                double valDifference = rs.getDouble("hodnota_rozdil");
                String device = rs.getString("pristroj_typ");
                double accuracy = rs.getDouble("pristroj_presnost");
                results.add(new SearchResultModel(date, val1, val2, valDifference, device, accuracy));
            }
            System.out.println("Num of results: " + results.size());
            rs.close();
            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return results;
    }

    private static String getStatement(String dateFrom, String dateTo, String valDiff,
                                       String measurementDesc, String deviceType, String pointLabel, String mUnit) {
        String statement = "SELECT datum_cas, hodnota_1, hodnota_2, hodnota_rozdil, pristroj_typ, pristroj_presnost FROM view_all "; // TODO hodnota_rozdil
        String st1 = null, st2 = null, st3 = null, st4 = null, st5 = null, st6 = null, st7 = null;

        //TODO PRASE. jsem PRASE....

        int from = Integer.MIN_VALUE;
        if (dateFrom != "") {
            from = (int)dateToTimestamp(dateFrom); // od 00:00:00
            st1 = " datum_cas >= " + from + " ";
        }

        int to = Integer.MIN_VALUE;
        if (dateTo != "") {
            to = (int) dateToTimestamp(dateTo) + 86399; // do 23:59:59 (86400 je sekund za den)
            st2 = " datum_cas <= " + to + " ";
        }

        double difference = Double.MIN_VALUE;
        if (valDiff != "" && valDiff.length() > 0) {
            difference = Double.parseDouble(valDiff);
            st3 = " max_odchylka < " + difference + "22 ";
        }

        String measurement;
        if (measurementDesc != "") {
            measurement = measurementDesc;
            st4 = " typ_mereni_popis = '" + measurement + "' ";
        }

        String device;
        if (deviceType != "") {
            device = deviceType;
            st5 = " pristroj_typ = '" + device + "' ";
        }

        String point;
        if (pointLabel != "") {
            point = pointLabel;
            st6 = " bod_popis = '" + point + "' ";
        }

        String unit;
        if (mUnit != "") {
            unit = mUnit;
            st7 = " velicina = '" + unit + "' ";
        }

        if (st1 != null || st2 != null || st3 != null || st4 != null || st5 != null || st6 != null || st7 != null)
            statement = statement + "WHERE (";

        if (st1 != null) statement = statement + st1;
            if (st2 != null) statement = statement + " AND ";
        if (st2 != null) statement = statement + st2;
            if (st3 != null) statement = statement + " AND ";
        if (st3 != null) statement = statement + st3;
            if (st4 != null) statement = statement + " AND ";
        if (st4 != null) statement = statement + st4;
            if (st5 != null) statement = statement + " AND ";
        if (st5 != null) statement = statement + st5;
            if (st6 != null) statement = statement + " AND ";
        if (st6 != null) statement = statement + st6;
            if (st7 != null) statement = statement + " AND ";
        if (st7 != null) statement = statement + st7;

        if (st1 != null || st2 != null || st3 != null || st4 != null || st5 != null || st6 != null || st7 != null)
            statement = statement + ")";

        exportStatement = statement;
        statement = statement + ";";
        return statement;
    }

    public static void exportResults(String outputFilePath) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            CopyManager copyManager = new CopyManager((BaseConnection) conn);
            FileWriter fileWriter = new FileWriter(outputFilePath);
            copyManager.copyOut("COPY (" + exportStatement + ") TO STDOUT WITH DELIMITER ';'", fileWriter);
            conn.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public static ArrayList<PointModel> getPoints() {
        ArrayList<PointModel> points = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            ResultSet rs = query.executeQuery( "SELECT * FROM bod ;" );
            while ( rs.next() ) {
                int id = rs.getInt("bod_id");
                double x = rs.getDouble("bod_x");
                double y = rs.getDouble("bod_y");
                String label = rs.getString("bod_popis");
                points.add(new PointModel(id, x, y, label));
            }
            System.out.println("Num of points: " + points.size());
            rs.close();
            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return points;
    }

    public static ArrayList<DeviceModel> getDevices() {
        ArrayList<DeviceModel> devices = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            ResultSet rs = query.executeQuery( "SELECT * FROM pristroj ;" );
            while ( rs.next() ) {
                String deviceId = rs.getString("pristroj_id");
                String devicetype = rs.getString("pristroj_typ");
                devices.add(new DeviceModel(deviceId, devicetype));
            }
            System.out.println("Num of devices: " + devices.size());
            rs.close();
            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return devices;
    }

    public static ArrayList<MeasurementTypeModel> getMeasurementTypes() {
        ArrayList<MeasurementTypeModel> mTypes = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            ResultSet rs = query.executeQuery( "SELECT * FROM typ_mereni ;" );
            while ( rs.next() ) {
                int mId = rs.getInt("typ_mereni_id");
                String mDesc = rs.getString("typ_mereni_popis");
                mTypes.add(new MeasurementTypeModel(mId, mDesc));
            }
            System.out.println("Num of measurement types: " + mTypes.size());
            rs.close();
            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return mTypes;
    }

    public static ArrayList<String> getUnits() {
        ArrayList<String> units = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            ResultSet rs = query.executeQuery( "SELECT DISTINCT velicina FROM mereni ;" );
            while ( rs.next() ) {
                units.add(rs.getString("velicina"));
            }
            System.out.println("Num of units: " + units.size());
            rs.close();
            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return units;
    }

    public static float dateToTimestamp(String dateIn) {
        // 2015-05-13 -> 1431468000
        Date date = Date.valueOf(dateIn);
        Timestamp stamp = new Timestamp(date.getTime());
        // System.out.println(stamp.getTime()/1000);
        return stamp.getTime()/1000;
    }

    public static String timestampToDate(long unixSeconds) {
        //timestamp jsou sekundy od roku 1970, tahle funkce bere milisekundy -> unixSeconds*1000
        Timestamp stamp = new Timestamp(unixSeconds*1000);
        Date date = new Date(stamp.getTime());
        return date.toString();
    }

    public static String timestampToFullDate(long unixSeconds) {
        //timestamp jsou sekundy od roku 1970, tahle funkce bere milisekundy -> unixSeconds*1000
        Timestamp stamp = new Timestamp(unixSeconds*1000);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return sdf.format(date);
    }

    public static void deleteSelectedDevice(String deviceType) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            String sql = "DELETE FROM pristroj where pristroj_typ = '" + deviceType + "';";
            query.executeUpdate(sql);
            conn.commit();

            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

    public static void deleteSelectedMeasurement(String measurementDesc) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            query = conn.createStatement();

            String sql = "DELETE FROM typ_mereni where typ_mereni_popis = '" + measurementDesc + "';";
            query.executeUpdate(sql);
            conn.commit();

            query.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }

}
