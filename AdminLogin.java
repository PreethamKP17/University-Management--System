package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLogin extends JFrame implements ActionListener {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn;

    AdminLogin() {
        setTitle("Admin Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Admin Username: "));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password: "));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel());
        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        add(loginBtn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE username=? AND password=? AND role='admin'";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                setVisible(false);
                new AdminDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
