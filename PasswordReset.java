package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PasswordReset extends JFrame implements ActionListener {

    JTextField emailField;
    JPasswordField newPasswordField;
    JButton resetBtn;
    String role;

    PasswordReset(String role) {
        this.role = role;

        setTitle("Reset Password - " + role.toUpperCase());
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel(role.equals("admin") ? "Username:" : "Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        add(newPasswordField);

        add(new JLabel());
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        add(resetBtn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String emailOrUsername = emailField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();

        if (emailOrUsername.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement stmt = null;

            if (role.equals("student")) {
                stmt = con.prepareStatement("UPDATE student SET password=? WHERE email=?");
                stmt.setString(1, newPassword);
                stmt.setString(2, emailOrUsername);

            } else if (role.equals("faculty")) {
                stmt = con.prepareStatement("UPDATE faculty SET password=? WHERE email=?");
                stmt.setString(1, newPassword);
                stmt.setString(2, emailOrUsername);

            } else if (role.equals("admin")) {
                stmt = con.prepareStatement("UPDATE users SET password=? WHERE username=? AND role='admin'");
                stmt.setString(1, newPassword);
                stmt.setString(2, emailOrUsername);
            }

            if (stmt != null) {
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Password Reset Successfully!");
                    setVisible(false);
                    if (role.equals("student")) new StudentLogin();
                    else if (role.equals("faculty")) new FacultyLogin();
                    else new AdminLogin(); // optional
                } else {
                    JOptionPane.showMessageDialog(this, "User not found!");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting password.");
        }
    }
}
