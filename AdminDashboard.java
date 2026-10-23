package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame implements ActionListener {

    JButton studentBtn, facultyBtn, logoutBtn, attendanceBtn, feeentryBtn;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JLabel headerLabel = new JLabel("Welcome, Admin", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        headerLabel.setForeground(new Color(40, 40, 40));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Center Panel with Buttons
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 25, 25));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 400, 60, 400));
        mainPanel.setBackground(new Color(245, 245, 245));

        studentBtn = createStyledButton("Manage Students");
        facultyBtn = createStyledButton("Manage Faculty");
        attendanceBtn = createStyledButton("Attendance Management");
        feeentryBtn = createStyledButton("Fee Entry");
        logoutBtn = createStyledButton("Logout");

        mainPanel.add(studentBtn);
        mainPanel.add(facultyBtn);
        mainPanel.add(attendanceBtn);
        mainPanel.add(feeentryBtn);
        mainPanel.add(logoutBtn);

        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners
        studentBtn.addActionListener(this);
        facultyBtn.addActionListener(this);
        attendanceBtn.addActionListener(this);
        feeentryBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 110, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == studentBtn) {
            setVisible(false);
            new StudentManagement();
        } else if (ae.getSource() == facultyBtn) {
            setVisible(false);
            new FacultyManagement();
        } else if (ae.getSource() == attendanceBtn) {
            setVisible(false);
            new AttendanceManagement();
        } else if (ae.getSource() == feeentryBtn) {
            dispose();
            new FeeEntryForm();
        } else if (ae.getSource() == logoutBtn) {
            dispose();
            new Login();
        }
    }

}
