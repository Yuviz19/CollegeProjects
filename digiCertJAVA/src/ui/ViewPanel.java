package ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db.CertificateDAO;
import model.Certificate;

public class ViewPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[] { "ID", "Holder", "Course", "Institution", "Grade",
                           "Issue Date", "Expiry Date", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTextField searchField = new JTextField(15);

    public ViewPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchRow = new JPanel();
        searchRow.add(new JLabel("Search:"));
        searchRow.add(searchField);
        JButton searchButton = new JButton("Search");
        searchRow.add(searchButton);
        JButton showAllButton = new JButton("Show all");
        searchRow.add(showAllButton);
        add(searchRow, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadTable());
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadTable();
        });
        searchField.addActionListener(e -> loadTable());
        loadTable();
    }

    private void loadTable() {
        CertificateDAO dao = new CertificateDAO();
        java.util.List<Certificate> list = searchField.getText().trim().isEmpty()
                ? dao.findAll() : dao.search(searchField.getText().trim());

        model.setRowCount(0);
        for (Certificate c : list) {
            model.addRow(new Object[] { c.id, c.holderName, c.course, c.institution,
                    c.grade, c.issueDate, c.expiryDate, c.status });
        }
    }
}
