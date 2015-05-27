import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by czerudla on 22.5.15.
 */
public class FrontEnd extends JFrame {
    private JTextField selectTextField;
    private JButton selectFileButton;
    private JPanel rootPanel;
    private JTextField doTextField;
    private JTextField odchylkaTextField;
    private JComboBox bodComboBox;
    private JComboBox mereniComboBox;
    private JComboBox pristrojComboBox;
    private JComboBox velicinaComboBox;
    private JTextField odTextField;
    private JButton exportDoCSVButton;
    private JButton hledejButton;
    private JButton loadFileButton;
    private JTable table;
    private JButton smazMereniButton;
    private JButton smazPristrojButton;


    public FrontEnd() {
        super("Magicka aplikace na RDB");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeValue();

        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv", "txt");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(FrontEnd.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    selectTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        hledejButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDataToTable();
            }
        });

        loadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String path = selectTextField.getText();
                File f = new File(path);
                if (f.exists()) {
                    DbFunctions.copyDataToDatabase(path);
                    selectTextField.setText("Data uložena do databáze");
                    initializeValue();
                } else {
                    selectTextField.setText("Není vybrán soubor");
                }
            }
        });

        smazMereniButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mereni = mereniComboBox.getSelectedItem().toString();
                int value = JOptionPane.showConfirmDialog(rootPanel,
                        "Opravdu chcete vymazat všechny měření: " + mereni + " z databáze");

                switch (value) {
                    case JOptionPane.CANCEL_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.YES_OPTION:
                        DbFunctions.deleteSelectedMeasurement(mereni);
                        setMereniComboBoxData();
                        break;
                }
            }
        });

        smazPristrojButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pristroj = pristrojComboBox.getSelectedItem().toString();
                int value = JOptionPane.showConfirmDialog(rootPanel,
                        "Opravdu chcete vymazat všechny " + pristroj + " z databáze?");

                switch (value) {
                    case JOptionPane.CANCEL_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.YES_OPTION:
                        DbFunctions.deleteSelectedDevice(pristroj);
                        setPristrojComboBoxData();
                        break;
                }

            }
        });

        exportDoCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory( new java.io.File(""));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                chooser.setAcceptAllFileFilterUsed(true);


                if(chooser.showSaveDialog(FrontEnd.this) == JFileChooser.APPROVE_OPTION){
                    String path = chooser.getSelectedFile().getAbsolutePath() + ".csv";
                    System.out.print(path);
                    DbFunctions.exportResults(path);
                }
            }
        });

        setVisible(true);
    }

    private void setBodComboBoxData() {
        bodComboBox.removeAllItems();
        ArrayList<PointModel> points = DbFunctions.getPoints();
        bodComboBox.addItem("");
        while (points.size() > 0) {
            bodComboBox.addItem(points.remove(0).getLabel());
        }
        bodComboBox.setVisible(true);
    }

    private void setMereniComboBoxData() {
        mereniComboBox.removeAllItems();
        ArrayList<MeasurementTypeModel> measurementTypeModels = DbFunctions.getMeasurementTypes();
        mereniComboBox.addItem("");
        while (measurementTypeModels.size() > 0) {
            mereniComboBox.addItem(measurementTypeModels.remove(0).getMeasurementDesc());
        }
    }

    private void setPristrojComboBoxData() {
        pristrojComboBox.removeAllItems();
        ArrayList<DeviceModel> devices = DbFunctions.getDevices();
        pristrojComboBox.addItem("");
        while (devices.size() > 0) {
            pristrojComboBox.addItem(devices.remove(0).getDeviceType());
        }

    }

    private void setVelicinaComboBoxData() {
        velicinaComboBox.removeAllItems();
        ArrayList<String> units = DbFunctions.getUnits();
        velicinaComboBox.addItem("");
        for (String unit : units) {
            velicinaComboBox.addItem(unit);
        }
    }

    private void showDataToTable() {
        ArrayList<SearchResultModel> searchResult = DbFunctions.selectByAllValues(
                odTextField.getText(),
                doTextField.getText(),
                odchylkaTextField.getText(),
                mereniComboBox.getSelectedItem().toString(),
                pristrojComboBox.getSelectedItem().toString(),
                bodComboBox.getSelectedItem().toString(),
                velicinaComboBox.getSelectedItem().toString()
        );

        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        String header[] = new String[] { "Datum", "Hodnota 1", "Hodnota 2", "Rozdíl", "Zařízení", "Přesnost" };
        dtm.setColumnIdentifiers(header);

        table.setModel(dtm);
        String date;
        double val1;
        double val2;
        double valDiff;
        String device;
        double accuracy;

        for (int i = 0; i < searchResult.size(); i++){
            date = DbFunctions.timestampToFullDate(searchResult.get(i).getDate());
            val1 = searchResult.get(i).getVal1();
            val2 = searchResult.get(i).getVal2();
            valDiff = searchResult.get(i).getValDiff();
            device = searchResult.get(i).getDevice();
            accuracy = searchResult.get(i).getAccuracy();

            dtm.insertRow(i, new Object[] {date, val1, val2, valDiff, device, accuracy});
        }
    }

    private void initializeValue() {
        setMereniComboBoxData();
        setPristrojComboBoxData();
        setBodComboBoxData();
        setVelicinaComboBoxData();
    }
}
