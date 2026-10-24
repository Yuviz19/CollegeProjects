package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
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

    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(18);
    private final JLabel infoLabel = new JLabel(" ");

    public ViewPanel() {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(Style.title("View / Search Certificates"), BorderLayout.NORTH);

        JPanel searchRow = new JPanel();
        searchRow.add(new JLabel("Search (ID, name, course):"));
        searchRow.add(searchField);
        JButton searchButton = Style.primaryButton("Search");
        searchRow.add(searchButton);
        JButton showAllButton = new JButton("Show all");
        searchRow.add(showAllButton);
        top.add(searchRow, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> loadTable());
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadTable();
        });
        searchField.addActionListener(e -> loadTable());
        setupTableLooks();
    }

    private void setupTableLooks() {
        table.setRowHeight(26);
        table.setFont(Style.NORMAL);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(Style.HEADING);
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer colored = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                if (!selected) {
                    String status = String.valueOf(model.getValueAt(row, 7));
                    String expiry = String.valueOf(model.getValueAt(row, 6));
                    if (status.equals("REVOKED")) {
                        c.setBackground(new Color(250, 218, 218));
                    } else if (isExpired(expiry)) {
                        c.setBackground(new Color(235, 235, 235));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, colored);
    }

    private boolean isExpired(String expiry) {
        try {
            return LocalDate.parse(expiry).isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    public void loadTable() {
        infoLabel.setText("Loading...");
        model.setRowCount(0);
        String searchText = searchField.getText().trim();

        new SwingWorker<java.util.List<Certificate>, Void>() {
            @Override
            protected java.util.List<Certificate> doInBackground() {
                CertificateDAO dao = new CertificateDAO();
                return searchText.isEmpty() ? dao.findAll() : dao.search(searchText);
            }

            @Override
            protected void done() {
                java.util.List<Certificate> list;
                try {
                    list = get();
                } catch (Exception ex) {
                    infoLabel.setText("Database error.");
                    return;
                }

                for (Certificate c : list) {
                    model.addRow(new Object[] {
                            c.getId(), c.getHolderName(), c.getCourse(), c.getInstitution(),
                            c.getGrade(), c.getIssueDate(), c.getExpiryDate(), c.getStatus()
                    });
                }
                infoLabel.setText(list.size() + " certificate(s) shown.");
            }
        }.execute();
    }
}
