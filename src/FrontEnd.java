import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by czerudla on 22.5.15.
 */
public class FrontEnd extends JFrame {
    private JTextField textField1;
    private JButton selectFileButton;
    private JTable table1;
    private JPanel rootPanel;

    public FrontEnd() {
        super("Magicka aplikace");
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(FrontEnd.this);
                textField1.setText(chooser.getSelectedFile().getName());
            }
        });

        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        String header[] = new String[] { "Datum", "Hodnota 1", "Hodnota 2", "Rozdíl", "Zařízení", "Přesnost" };
        dtm.setColumnIdentifiers(header);

        table1.setModel(dtm);

        // add row dynamically into the table
        for (int count = 1; count <= 30; count++) {
            dtm.addRow(new Object[] { "data", "data", "data",
                    "data", "data", "data" });
        }

        setVisible(true);
    }
}