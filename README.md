# 🎓 University Management System

A comprehensive **Java Swing-based desktop application** using **JDBC** and **MySQL** for managing university operations, including student and faculty management, attendance tracking, fee handling, and administrative functions.

---

## 🚀 Features

### 👨‍💼 Admin Dashboard
- Manage Students (Add, Update, Delete, View)
- Manage Faculty (Add, Update, Delete, View)
- Fee Management
- Attendance Management
- Mark Entry System

### 👨‍🎓 Student Dashboard
- View Personal Profile & Picture
- Check Attendance Records
- View Marks / Grades
- Fee Status Overview

### 👩‍🏫 Faculty Dashboard
- Mark Student Attendance
- Add / Update Student Marks
- View Assigned Student Info
- Manage Course Content

---

## 🛠 Technology Stack

- **Frontend**: Java Swing (GUI)
- **Backend**: Java
- **Database**: MySQL
- **Connector**: MySQL Connector/J (JDBC)
- **IDE**: Eclipse / IntelliJ IDEA / NetBeans (Recommended)

---

## ⚙️ Prerequisites

- Java Development Kit (JDK 8 or higher)
- MySQL Server installed and running
- MySQL Connector/J (JDBC Driver)
- Eclipse or any Java IDE (Optional)

---

## 🧾 Database Setup

1. Install and start your MySQL server.
2. Create a new database named `university`.
3. Import the schema:
   ```sql
source SQL/queries.sql;
```

---

## 🔧 Configuration

Edit `DBConnection.java`:
```java
conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/university", 
    "your_username", 
    "your_password"
);
```

Replace `your_username` and `your_password` with your MySQL credentials.

---

## 🖥 Installation & Running

1. Clone this repository:
   ```bash
git clone https://github.com/your-repo/university-management-system.git
```
2. Set up the database as described above.
3. Add MySQL Connector/J to your classpath.
4. Compile and run the app:
   ```bash
javac -cp ".:mysql-connector-java.jar" *.java
   java -cp ".:mysql-connector-java.jar" com.university.Login
```

---

## 🔐 Default Login Credentials for admin
 
| Role   | Username | Password    |
|--------|----------|-------------|
| Admin  | admin1   | adminpass   |


for student and faculty login using email and password stored in database.

> You can add more users via the Admin Dashboard.

---

## 🧭 Project Structure

```
UniversityManagementSystem/
├── Login.java                # Role selection and login
├── AdminLogin.java           # Admin auth
├── StudentLogin.java         # Student auth
├── FacultyLogin.java         # Faculty auth
├── AdminDashboard.java       # Admin interface
├── StudentDashboard.java     # Student interface
├── FacultyDashboard.java     # Faculty interface
├── StudentManagement.java    # CRUD for students
├── FacultyManagement.java    # CRUD for faculty
├── AttendanceManagement.java # Attendance interface
├── MarkAttendance.java       # Faculty marks attendance
├── AddMarks.java             # Marks entry
├── FeeEntryForm.java         # Fee management
├── DBConnection.java         # JDBC connection
├── PasswordReset.java        # Password change utility
├── BackgroundPanel.java      # Utility class
├── SQL/
│   ├── queries.sql           # Database schema
│   └── query-input.sql       # Additional inserts
└── icons/
    └── defaultProfile.png    # Default profile pic
```

---

## ⚠️ Common Issues

1. **❌ Database Connection Error**
   - Ensure MySQL is running
   - Check DB credentials in `DBConnection.java`
   - Verify port and hostname

2. **❌ ClassNotFoundException**
   - Make sure MySQL Connector/J is added to the classpath

3. **❌ GUI Not Displaying Properly**
   - Verify JDK version
   - Use Swing-compatible Java environment

---

## 🤝 Contributing

1. Fork this repository
2. Create a new branch (`git checkout -b feature-xyz`)
3. Make changes and commit (`git commit -m "Add feature XYZ"`)
4. Push to GitHub (`git push origin feature-xyz`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 📬 Support

If you encounter issues or have questions, [create an issue](https://github.com/your-repo/university-management-system/issues) on GitHub.