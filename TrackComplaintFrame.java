package ui;

import dao.ComplaintDAO;
import model.Complaint;

import javax.swing.*;
import java.awt.*;

public class TrackComplaintFrame extends JFrame {

    private final JTextField idField = new JTextField(10);
    private final JTextArea output = new JTextArea();
    private final JButton upvoteBtn = new JButton("I'm affected too (Upvote)");
    private final ComplaintDAO dao = new ComplaintDAO();
    private Complaint current;

    public TrackComplaintFrame() {
        setTitle("Track Complaint");
        setSize(480, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JButton searchBtn = new JButton("Search");
        top.add(new JLabel("Complaint ID:"));
        top.add(idField);
        top.add(searchBtn);
        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));
        output.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JScrollPane(output), BorderLayout.CENTER);

        upvoteBtn.setEnabled(false);
        JPanel bottom = new JPanel();
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));
        bottom.add(upvoteBtn);
        add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> search());
        idField.addActionListener(e -> search());
        upvoteBtn.addActionListener(e -> upvote());
    }

    private void search() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            current = dao.getComplaintById(id);
            if (current == null) {
                output.setText("No complaint found with ID " + id);
                upvoteBtn.setEnabled(false);
                return;
            }
            show(current);
            upvoteBtn.setEnabled(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid numeric ID.", "Invalid input",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void upvote() {
        if (current == null) return;
        try {
            if (dao.upvote(current.getId())) {
                current = dao.getComplaintById(current.getId());
                show(current);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void show(Complaint c) {
        output.setText(
                "Complaint ID : " + c.getId() + "\n" +
                        "Title        : " + c.getTitle() + "\n" +
                        "Category     : " + c.getCategory() + "\n" +
                        "Location     : " + c.getLocation() + "\n" +
                        "Reported on  : " + c.getCreatedAt() + "\n" +
                        "Status       : " + c.getStatus() + "\n" +
                        "Priority     : " + c.getPriorityLevel() + " (" + c.getPriorityScore() + "/100)\n" +
                        "Upvotes      : " + c.getUpvotes() + "\n\n" +
                        "Description:\n" + c.getDescription() + "\n\n" +
                        "Admin Remarks:\n" + (c.getAdminRemarks() == null ? "-" : c.getAdminRemarks()));
        output.setCaretPosition(0);
    }
}
