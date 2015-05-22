import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileReader;
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

    public static ArrayList<SearchResultModel> selectByDate(String dateFrom, String dateTo) {
        ArrayList<SearchResultModel> results = new ArrayList<SearchResultModel>();
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(dbAdress, dbLogin, dbPass);
            conn.setAutoCommit(false);

            int from = (int)dateToTimestamp(dateFrom); // od 00:00:00
            int to = (int)dateToTimestamp(dateTo)+86399; // do 23:59:59 (86400 je sekund za den)

            query = conn.createStatement();

            ResultSet rs = query.executeQuery( "SELECT * FROM view_all WHERE (datum_cas >= " + from + " AND datum_cas <= " + to + ") ;" );
            while ( rs.next() ) {
                int date = rs.getInt("datum_cas");
                double val1 = rs.getDouble("hodnota_1");
                double val2 = rs.getDouble("hodnota_2");
                String device = rs.getString("pristroj_id");
                double accuracy = 0; //TODO accuracy
                results.add(new SearchResultModel(date, val1, val2, device, accuracy));
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

    public static ArrayList<PointModel> getPoints() {
        ArrayList<PointModel> points = new ArrayList<PointModel>();
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
        ArrayList<DeviceModel> devices = new ArrayList<DeviceModel>();
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

    public static float dateToTimestamp(String dateIn) {
        // 2015-05-13 -> 1431468000
        Date date = Date.valueOf(dateIn);
        Timestamp stamp = new Timestamp(date.getTime());
        System.out.println(stamp.getTime()/1000);
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
        Date date = new Date(unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return sdf.format(date);
    }

}
