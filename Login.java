package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {

    JComboBox<String> roleBox;
    JButton proceedBtn;

    Login() {
        setTitle("University Management - Role Selection");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        add(new JLabel("Select Role:", SwingConstants.CENTER));
        String[] roles = {"admin", "student", "faculty"};
        roleBox = new JComboBox<>(roles);
        JPanel panel = new JPanel();
        panel.add(roleBox);
        add(panel);

        proceedBtn = new JButton("Proceed to Login");
        proceedBtn.addActionListener(this);
        add(proceedBtn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String role = (String) roleBox.getSelectedItem();

        setVisible(false);
        switch (role) {
            case "admin":
            	dispose();
                new AdminLogin(); break;
            case "student":
            	dispose();
                new StudentLogin(); break;
            case "faculty": 
            	dispose();
                new FacultyLogin(); break;
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
