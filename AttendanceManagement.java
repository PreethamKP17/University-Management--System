package com.university;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;

public class AttendanceManagement extends JFrame implements ActionListener{
	
	JComboBox<String> studentCombo;
	JTextField dateField;
	JRadioButton presentBtn, absentBtn;
	JButton markBtn, viewBtn, backBtn;
	JTable table;
	DefaultTableModel model;
	ButtonGroup statusGroup;
	
	public AttendanceManagement() {
		setTitle("Attendance Management");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		//Top JPanel
		
		JPanel topPanel = new JPanel(new GridLayout(2,4,20,10));
		topPanel.setBorder(BorderFactory.createTitledBorder("Mark Attedence"));
		
		dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

		studentCombo = new JComboBox<>();
		loadStudentNames();
		
		presentBtn = new JRadioButton("Present");
		absentBtn = new JRadioButton("Absent");
		statusGroup = new ButtonGroup();
		statusGroup.add(presentBtn);
		statusGroup.add(absentBtn);
		
		topPanel.add(new JLabel("Select Student : "));
		topPanel.add(studentCombo);
		topPanel.add(new JLabel("Date : "));
		topPanel.add(dateField);
		
		topPanel.add(new JLabel("Status : "));
		topPanel.add(presentBtn);
		topPanel.add(absentBtn);
		
		markBtn = new JButton("Mark Attendance");
		markBtn.addActionListener(this);
		viewBtn = new JButton("View Attendance");
		viewBtn.addActionListener(this);
		backBtn = new JButton("Back");
		backBtn.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(markBtn);
		buttonPanel.add(viewBtn);
		buttonPanel.add(backBtn);
		
		//Table
		model = new DefaultTableModel(new String[] {"ID", "Student","Date","Status"},0);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		
		//Layout
		setLayout(new BorderLayout(10,10));
		add(topPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
		add(scrollPane, BorderLayout.SOUTH);
		
		setVisible(true);
		
	}
	private void loadStudentNames() {
		try {
			Connection con = DBConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT id, name From student");
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				studentCombo.addItem(id + " - " + name);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadAttendance() {
		model.setRowCount(0);
		try {
			 Connection con = DBConnection.getConnection();
	         String query = "SELECT a.id, s.name, a.date, a.status FROM attendance a JOIN student s ON a.student_id = s.id";
	         Statement stmt = con.createStatement();
	         ResultSet rs = stmt.executeQuery(query);
	         while(rs.next()) {
	        	 model.addRow(new Object[] {
	        			 rs.getInt("id"),
	        			 rs.getString("name"),
	        			 rs.getDate("date"),
	        			 rs.getString("status")
	        	 });
	         }
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == markBtn) {
			String selected = (String) studentCombo.getSelectedItem();
			if(selected == null || (!presentBtn.isSelected() && !absentBtn.isSelected())) {
				JOptionPane.showMessageDialog(this,  "Please select student and status.");
				return;
			}
			
			int studentId = Integer.parseInt(selected.split(" - ")[0]);
			String date = dateField.getText();
			String status = presentBtn.isSelected() ? "Present" : "Absent";
			
			try {
				Connection con = DBConnection.getConnection();
				String checkquery = "SELECT * FROM attendance WHERE student_id=? AND date=?";
				PreparedStatement stmt =con.prepareStatement(checkquery);
				stmt.setInt(1, studentId);
				stmt.setString(2, date);
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					JOptionPane.showMessageDialog(this, "Attendance already marked for this date");
					return;
				}
				
				//Insert attendance
				String query = "INSERT INTO attendance (student_id, date,status) VALUES(?,?,?)";
				PreparedStatement ps =con.prepareStatement(query);
				ps.setInt(1, studentId);
				ps.setString(2, date);
				ps.setString(3, status);
				ps.executeUpdate();
				
				JOptionPane.showMessageDialog(this, "Attendance marked");
				
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}else if(ae.getSource() == viewBtn) {
			loadAttendance();
		}else if(ae.getSource() == backBtn) {
			dispose();
			new AdminDashboard();
		}
	}

}
