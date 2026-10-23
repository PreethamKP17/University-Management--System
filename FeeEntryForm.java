package com.university;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FeeEntryForm extends JFrame{

	JComboBox<Integer> studentIdBox;
	JTextField amountField;
	JComboBox<String> statusBox;
	JButton addFeeBtn, backBtn;
	
	public FeeEntryForm() {
		setTitle("Add Fee Record");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridLayout(5,2,10,10));
		
		panel.add(new JLabel("Student ID : "));
		studentIdBox = new JComboBox<>();
		loadStudentIds();
		panel.add(studentIdBox);
		
		panel.add(new JLabel("Amount : "));
		amountField = new JTextField();
		panel.add(amountField);
		
		panel.add(new JLabel("Status : "));
		statusBox = new JComboBox<>(new String[]{"Paid","Pending"});
		panel.add(statusBox);
		
		addFeeBtn = new JButton("Add Fee");
		addFeeBtn.addActionListener(e -> addFee());
		panel.add(addFeeBtn);
		
		backBtn = new JButton("Back to Dashboard");
		backBtn.addActionListener(e -> {
			dispose();
			new AdminDashboard();
		});
		panel.add(backBtn);
		
		//Adding padding around Form
		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.setBorder(BorderFactory.createEmptyBorder(100,300,100,300));
		outerPanel.add(panel, BorderLayout.CENTER);
		
		add(outerPanel);
		
		setVisible(true);
		
	}
	
	private void loadStudentIds() {
		try {
			Connection con = DBConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id FROM student");
			while(rs.next()) {
				studentIdBox.addItem(rs.getInt("id"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addFee() {
		if(amountField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Amount field cannot be empty.");
			return;
		}
		int studentId = (Integer) studentIdBox.getSelectedItem();
		double amount = Double.parseDouble(amountField.getText());
		String status = (String) statusBox.getSelectedItem();
		java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
		
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO fees (student_id, amount, status, date) VALUES (?,?,?,?)");
			ps.setInt(1, studentId);
			ps.setDouble(2, amount);
			ps.setString(3, status);
			ps.setDate(4, date);
			ps.executeUpdate();
			JOptionPane.showMessageDialog(this, "Fee record added!");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
