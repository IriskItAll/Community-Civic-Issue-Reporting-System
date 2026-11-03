package ui;

import dao.ComplaintDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class DashboardFrame extends JFrame {

    private final ComplaintDAO dao = new ComplaintDAO();
    private final JLabel totalLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel pendingLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel progressLbl = new JLabel("0", SwingConstants.CENTER);
    private final JLabel resolvedLbl = new JLabel("0", SwingConstants.CENTER);

    public DashboardFrame() {
        setTitle("CivicTrack - Community Civic Reporting System");
        setSize(650, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel header = new JLabel("CivicTrack", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Report. Track. Resolve.", SwingConstants.CENTER);
        sub.setForeground(Color.WHITE);
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(new Color(30, 90, 160));
        top.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        top.add(header);
        top.add(sub);
        add(top, BorderLayout.NORTH);

        // Stats cards
        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 10));
        stats.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        stats.add(makeCard("Total", totalLbl, new Color(60, 60, 60)));
        stats.add(makeCard("Pending", pendingLbl, new Color(200, 120, 0)));
        stats.add(makeCard("In Progress", progressLbl, new Color(30, 100, 200)));
        stats.add(makeCard("Resolved", resolvedLbl, new Color(30, 140, 60)));
        add(stats, BorderLayout.CENTER);

        // Buttons
        JButton reportBtn = new JButton("Report an Issue");
        JButton trackBtn = new JButton("Track Complaint");
        JButton adminBtn = new JButton("Admin Login");
        for (JButton b : new JButton[]{reportBtn, trackBtn, adminBtn}) {
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
        }
        JPanel bottom = new JPanel(new GridLayout(1, 3, 15, 15));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 25, 20));
        bottom.add(reportBtn);
        bottom.add(trackBtn);
        bottom.add(adminBtn);
        add(bottom, BorderLayout.SOUTH);

        reportBtn.addActionListener(e -> new ReportIssueFrame().setVisible(true));
        trackBtn.addActionListener(e -> new TrackComplaintFrame().setVisible(true));
        adminBtn.addActionListener(e -> adminLogin());

        // refresh stats whenever this window regains focus
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) { loadStats(); }
        });
        loadStats();
    }

    private JPanel makeCard(String title, JLabel value, Color color) {
        value.setFont(new Font("SansSerif", Font.BOLD, 30));
        value.setForeground(color);
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.add(value, BorderLayout.CENTER);
        p.add(t, BorderLayout.SOUTH);
        return p;
    }

    private void loadStats() {
        try {
            Map<String, Integer> m = dao.getStatusCounts();
            int p = m.get("Pending"), i = m.get("In Progress"), r = m.get("Resolved");
            pendingLbl.setText(String.valueOf(p));
            progressLbl.setText(String.valueOf(i));
            resolvedLbl.setText(String.valueOf(r));
            totalLbl.setText(String.valueOf(p + i + r));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminLogin() {
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        Object[] fields = {"Username:", user, "Password:", pass};
        int opt = JOptionPane.showConfirmDialog(this, fields, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;
        try {
            if (dao.validateAdmin(user.getText().trim(), new String(pass.getPassword()))) {
                new AdminDashboardFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}