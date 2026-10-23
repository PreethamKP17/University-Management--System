package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyLogin extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField passwordField;
    JButton loginBtn, resetBtn;

    FacultyLogin() {
        setTitle("Faculty Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        resetBtn = new JButton("Forgot Password?");
        resetBtn.addActionListener(this);
        add(resetBtn);

        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        add(loginBtn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password cannot be empty.");
                return;
            }

            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement stmt = con.prepareStatement(
                    "SELECT * FROM faculty WHERE email = ? AND password = ? AND role = 'faculty'"
                );
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) { 
                    int facultyID = rs.getInt("id");
                    JOptionPane.showMessageDialog(this, "Faculty Login Successful!");
                    setVisible(false);
                    new FacultyDashboard(facultyID);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Credentials or Role mismatch!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error occurred!");
            }
        } else if (e.getSource() == resetBtn) {
            new PasswordReset("faculty");
        }
    }
}
