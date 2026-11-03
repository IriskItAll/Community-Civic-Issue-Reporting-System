package ui;

import dao.ComplaintDAO;
import model.Complaint;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private final ComplaintDAO dao = new ComplaintDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> filterBox =
            new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Resolved"});
    private final JComboBox<String> statusBox =
            new JComboBox<>(new String[]{"Pending", "In Progress", "Resolved"});
    private final JTextField remarksField = new JTextField(25);
    private final JTextArea detailArea = new JTextArea(4, 40);
    private List<Complaint> currentList;

    public AdminDashboardFrame() {
        setTitle("CivicTrack - Admin Dashboard");
        setSize(950, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Top: filter + refresh
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton refreshBtn = new JButton("Refresh");
        top.add(new JLabel("Filter by status:"));
        top.add(filterBox);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        // Center: table
        String[] cols = {"ID", "Title", "Category", "Location", "Priority", "Score", "Status", "Upvotes", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom: details + update controls
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        JButton updateBtn = new JButton("Update Status");
        JButton deleteBtn = new JButton("Delete");
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controls.add(new JLabel("Status:"));
        controls.add(statusBox);
        controls.add(new JLabel("Remarks:"));
        controls.add(remarksField);
        controls.add(updateBtn);
        controls.add(deleteBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottom.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // Events
        refreshBtn.addActionListener(e -> loadComplaints());
        filterBox.addActionListener(e -> loadComplaints());
        updateBtn.addActionListener(e -> updateSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails();
        });

        loadComplaints();
    }

    public void loadComplaints() {
        try {
            currentList = dao.getComplaints((String) filterBox.getSelectedItem());
            model.setRowCount(0);
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            for (Complaint c : currentList) {
                model.addRow(new Object[]{
                        c.getId(), c.getTitle(), c.getCategory(), c.getLocation(),
                        c.getPriorityLevel(), c.getPriorityScore(), c.getStatus(),
                        c.getUpvotes(), fmt.format(c.getCreatedAt())
                });
            }
            detailArea.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || currentList == null || row >= currentList.size()) return;
        Complaint c = currentList.get(row);
        detailArea.setText("Reporter: " + c.getReporterName() + "  |  Contact: " + c.getReporterContact() +
                "\n\n" + c.getDescription());
        statusBox.setSelectedItem(c.getStatus());
        remarksField.setText(c.getAdminRemarks() == null ? "" : c.getAdminRemarks());
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a complaint first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try {
            dao.updateStatus(id, (String) statusBox.getSelectedItem(), remarksField.getText().trim());
            loadComplaints();
            JOptionPane.showMessageDialog(this, "Complaint #" + id + " updated.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a complaint first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this, "Delete complaint #" + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.deleteComplaint(id);
            loadComplaints();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
