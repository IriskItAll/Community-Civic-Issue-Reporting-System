CivicTrack

A desktop complaint management system built with Java and MySQL. Citizens can report civic issues and track their progress, while administrators review and manage the complaints from a separate dashboard.

I built this to practice connecting a Java GUI application to a relational database using JDBC, and to model a real workflow: report, prioritise, track, resolve.

Features

Report an issue: submit a complaint through a form, saved directly to the database
Track a complaint: look up a submitted complaint and check its current status
Admin dashboard: view all complaints and update their status
Priority calculation: each complaint is assigned a priority so urgent issues stand out
Reports: summary view of complaints for administrators

Tech Stack
Java (Swing for the GUI)
MySQL
JDBC
Maven

## 📂 Project Structure

```text
CivicTrack/
│
├── Main.java
│
├── DBConnection.java
├── Complaint.java
├── ComplaintDAO.java
├── PriorityCalculator.java
│
├── DashboardFrame.java
├── AdminDashboardFrame.java
├── ReportIssueFrame.java
├── TrackComplaintFrame.java
│
├── civictrack_database.sql
├── pom.xml
└── .gitignore
```

### File Descriptions

| File | Description |
|---|---|
| `Main.java` | Application entry point |
| `DBConnection.java` | Handles database connectivity |
| `Complaint.java` | Complaint model class |
| `ComplaintDAO.java` | Handles complaint database operations |
| `PriorityCalculator.java` | Calculates complaint priority |
| `DashboardFrame.java` | Main application dashboard |
| `AdminDashboardFrame.java` | Admin dashboard |
| `ReportIssueFrame.java` | Complaint submission interface |
| `TrackComplaintFrame.java` | Complaint tracking interface |
| `civictrack_database.sql` | Database schema |
| `pom.xml` | Maven configuration |
| `.gitignore` | Files excluded from Git tracking |

Getting Started
Prerequisites
JDK 8 or later
MySQL Server
Maven
An IDE such as IntelliJ IDEA
1. Clone the repository
bash
git clone https://github.com/IriskItAll/CivicTrack.git
cd CivicTrack
2. Set up the database

Run the SQL script in MySQL (Workbench or the command line):

bash
mysql -u root -p < civictrack_database.sql
3. Configure the connection

Open DBConnection.java and update the connection details to match your local MySQL setup:

java
String url = "jdbc:mysql://localhost:3306/your_database_name";
String username = "your_username";
String password = "your_password";
4. Build and run
bash
mvn clean install

Then open the project in your IDE and run Main.java.

screenshots
Main Dashboard


<img width="796" height="553" alt="image" src="https://github.com/user-attachments/assets/7e6b6e9e-7346-41dd-8690-7f853127a8ea" />

Admin Dashboard


<img width="1171" height="717" alt="image" src="https://github.com/user-attachments/assets/75f4ec8c-2afa-4a75-b54f-9d0186a48182" />

Report An Issue


<img width="586" height="642" alt="image" src="https://github.com/user-attachments/assets/5c812775-3536-4832-969e-9ceecb07d8ee" />

Track Complaint


<img width="585" height="593" alt="image" src="https://github.com/user-attachments/assets/0b9afc8d-def3-4554-9ce6-0174971eb703" />





Possible Improvements
Login system with separate user and admin roles
Search and filter options for complaints
Email notifications when a complaint status changes
Charts and analytics in the reports section


License

Built for learning purposes. Feel free to use it as a reference.
