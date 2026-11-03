package dao;

import database.DBConnection;
import model.Complaint;
import model.PriorityCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ComplaintDAO {

    // ---------- CREATE ----------
    /** Inserts a complaint and returns the generated complaint ID (or -1 on failure). */
    public int addComplaint(Complaint c) throws SQLException {
        int score = PriorityCalculator.calculate(c.getCategory(), c.getDescription(), 0, 0);
        c.setPriorityScore(score);
        c.setPriorityLevel(PriorityCalculator.getLevel(score));

        String sql = "INSERT INTO complaints (title, category, description, location, reporter_name, " +
                "reporter_contact, priority_score, priority_level) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getReporterName());
            ps.setString(6, c.getReporterContact());
            ps.setInt(7, c.getPriorityScore());
            ps.setString(8, c.getPriorityLevel());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ---------- READ ----------
    public Complaint getComplaintById(int id) throws SQLException {
        String sql = "SELECT * FROM complaints WHERE complaint_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** status = "All" returns everything. Sorted by highest priority first. */
    public List<Complaint> getComplaints(String status) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        boolean all = status == null || status.equals("All");
        String sql = "SELECT * FROM complaints " + (all ? "" : "WHERE status = ? ") +
                "ORDER BY priority_score DESC, created_at ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (!all) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ---------- UPDATE ----------
    public boolean updateStatus(int id, String status, String remarks) throws SQLException {
        String sql = "UPDATE complaints SET status = ?, admin_remarks = ? WHERE complaint_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, remarks);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** "Me too" upvote - increases upvotes and recalculates the priority. */
    public boolean upvote(int id) throws SQLException {
        Complaint c = getComplaintById(id);
        if (c == null) return false;

        int newVotes = c.getUpvotes() + 1;
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - c.getCreatedAt().getTime());
        int score = PriorityCalculator.calculate(c.getCategory(), c.getDescription(), newVotes, days);

        String sql = "UPDATE complaints SET upvotes = ?, priority_score = ?, priority_level = ? WHERE complaint_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newVotes);
            ps.setInt(2, score);
            ps.setString(3, PriorityCalculator.getLevel(score));
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------
    public boolean deleteComplaint(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM complaints WHERE complaint_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- STATS ----------
    public Map<String, Integer> getStatusCounts() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Pending", 0);
        map.put("In Progress", 0);
        map.put("Resolved", 0);
        String sql = "SELECT status, COUNT(*) AS total FROM complaints GROUP BY status";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("status"), rs.getInt("total"));
        }
        return map;
    }

    // ---------- ADMIN LOGIN ----------
    public boolean validateAdmin(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM admins WHERE username = ? AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ---------- helper ----------
    private Complaint mapRow(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("complaint_id"));
        c.setTitle(rs.getString("title"));
        c.setCategory(rs.getString("category"));
        c.setDescription(rs.getString("description"));
        c.setLocation(rs.getString("location"));
        c.setReporterName(rs.getString("reporter_name"));
        c.setReporterContact(rs.getString("reporter_contact"));
        c.setPriorityScore(rs.getInt("priority_score"));
        c.setPriorityLevel(rs.getString("priority_level"));
        c.setStatus(rs.getString("status"));
        c.setUpvotes(rs.getInt("upvotes"));
        c.setAdminRemarks(rs.getString("admin_remarks"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}