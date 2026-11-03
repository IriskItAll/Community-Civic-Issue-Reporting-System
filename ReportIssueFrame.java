package ui;

import dao.ComplaintDAO;
import model.Complaint;
import model.PriorityCalculator;

import javax.swing.*;
import java.awt.*;

public class ReportIssueFrame extends JFrame {

    private final JTextField titleField = new JTextField(25);
    private final JComboBox<String> categoryBox = new JComboBox<>(PriorityCalculator.CATEGORIES);
    private final JTextField locationField = new JTextField(25);
    private final JTextArea descArea = new JTextArea(5, 25);
    private final JTextField nameField = new JTextField(25);
    private final JTextField contactField = new JTextField(25);
    private final ComplaintDAO dao = new ComplaintDAO();

    public ReportIssueFrame() {
        setTitle("Report an Issue");
        setSize(480, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        addRow(form, g, 0, "Title:", titleField);
        addRow(form, g, 1, "Category:", categoryBox);
        addRow(form, g, 2, "Location:", locationField);
        addRow(form, g, 3, "Description:", new JScrollPane(descArea));
        addRow(form, g, 4, "Your Name:", nameField);
        addRow(form, g, 5, "Phone / Email:", contactField);

        JButton submit = new JButton("Submit Complaint");
        submit.setFont(new Font("SansSerif", Font.BOLD, 14));
        submit.addActionListener(e -> submitComplaint());

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        JPanel south = new JPanel();
        south.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        south.add(submit);
        add(south, BorderLayout.SOUTH);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row;
        g.gridx = 0; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void submitComplaint() {
        String title = titleField.getText().trim();
        String loc = locationField.getText().trim();
        String desc = descArea.getText().trim();
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();

        if (title.isEmpty() || loc.isEmpty() || desc.isEmpty() || name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing data",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint c = new Complaint(title, (String) categoryBox.getSelectedItem(), desc, loc, name, contact);
        try {
            int id = dao.addComplaint(c);
            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                        "Complaint submitted!\n\nYour Complaint ID: " + id +
                                "\nPriority: " + c.getPriorityLevel() + " (" + c.getPriorityScore() + ")\n\n" +
                                "Use this ID to track your complaint.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not save complaint.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}