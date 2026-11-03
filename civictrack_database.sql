DROP DATABASE IF EXISTS civictrack;
CREATE DATABASE civictrack;
USE civictrack;

-- Admin users (demo: plain text password; hash it in a real system)
CREATE TABLE admins (
    admin_id  INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL
);

-- Complaints
CREATE TABLE complaints (
    complaint_id     INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150) NOT NULL,
    category         VARCHAR(50)  NOT NULL,
    description      TEXT         NOT NULL,
    location         VARCHAR(200) NOT NULL,
    reporter_name    VARCHAR(100) NOT NULL,
    reporter_contact VARCHAR(100) NOT NULL,
    priority_score   INT          NOT NULL DEFAULT 0,
    priority_level   VARCHAR(20)  NOT NULL DEFAULT 'Low',
    status           ENUM('Pending','In Progress','Resolved') NOT NULL DEFAULT 'Pending',
    upvotes          INT          NOT NULL DEFAULT 0,
    admin_remarks    VARCHAR(255) DEFAULT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_status   ON complaints(status);
CREATE INDEX idx_priority ON complaints(priority_score);

-- Sample data
INSERT INTO admins (username, password) VALUES ('admin', 'admin123');

INSERT INTO complaints (title, category, description, location, reporter_name, reporter_contact, priority_score, priority_level, status, upvotes)
VALUES
('Deep pothole near school', 'Road / Pothole', 'Large pothole, dangerous for children and two-wheelers', 'Anna Nagar 2nd Ave', 'Ravi', '9876543210', 55, 'High', 'Pending', 3),
('No water supply for 3 days', 'Water Supply', 'Urgent: entire street has no water supply', 'T Nagar Main Road', 'Meena', '9123456780', 62, 'High', 'In Progress', 8),
('Street light not working', 'Street Light', 'Light is off for a week', 'Velachery Bus Stop', 'Karthik', 'karthik@mail.com', 20, 'Low', 'Pending', 1);

-- Useful queries (for your viva / report)
-- SELECT status, COUNT(*) FROM complaints GROUP BY status;
-- SELECT * FROM complaints ORDER BY priority_score DESC;
-- SELECT category, COUNT(*) AS total FROM complaints GROUP BY category;
