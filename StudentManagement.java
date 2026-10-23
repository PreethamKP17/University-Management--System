package com.university;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagement extends JFrame implements ActionListener {
	
	JTextField nameField, emailField, courseField, semesterField, searchField;
	JButton addBtn, updateBtn, deleteBtn, backBtn, searchBtn, resetBtn;
	JComboBox<String> searchFieldBox;
	JTable table;
	DefaultTableModel model;
	
	int selectedId = -1;
	
	public StudentManagement() {
		setTitle("StudentMangement");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Form Panel
		JPanel formPanel = new JPanel(new GridLayout(2,4,20,20));
		formPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Student Details"));
		
		nameField = new JTextField();
		emailField = new JTextField();
		courseField = new JTextField();
		semesterField = new JTextField();
		
		Dimension fieldSize = new Dimension(200,25);
		nameField.setPreferredSize(fieldSize);
		emailField.setPreferredSize(fieldSize);
		courseField.setPreferredSize(fieldSize);
		semesterField.setPreferredSize(fieldSize);
		
		formPanel.add(new JLabel("Name : "));
		formPanel.add(new JLabel("Email : "));
		formPanel.add(new JLabel("Course : "));
		formPanel.add(new JLabel("Semester : "));
		
		formPanel.add(nameField);
		formPanel.add(emailField);
		formPanel.add(courseField);
		formPanel.add(semesterField);
		
		addBtn = new JButton("Add");
		updateBtn = new JButton("Update");
		deleteBtn = new JButton("Delete");
		backBtn = new JButton("Back");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,4,10,10));
		buttonPanel.add(addBtn);
		buttonPanel.add(updateBtn);
		buttonPanel.add(deleteBtn);
		buttonPanel.add(backBtn);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		
		//Search Panel
		JPanel searchPanel = new JPanel();
		searchPanel.setBorder(BorderFactory.createTitledBorder("Search Students"));
		
		JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
		searchWrapper.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
		searchWrapper.add(searchPanel);
		
		String[] fields = {"Name","Email","Course","Semester"};
		searchFieldBox = new JComboBox<String>(fields);
		searchField = new JTextField(20); 
		searchBtn = new JButton("Search");
		resetBtn = new JButton("Reset");
		
		searchPanel.add(new JLabel("Search by : "));
		searchPanel.add(searchFieldBox);
		searchPanel.add(searchField);
		searchPanel.add(searchBtn);
		searchPanel.add(resetBtn);	
		
		addBtn.addActionListener(this);
		updateBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		backBtn.addActionListener(this);
		searchBtn.addActionListener(this);
		resetBtn.addActionListener(this);
		
		model = new DefaultTableModel(new String[] {"ID","NAME","EMAIL","COURSE","SEMESTER"},0);
		table = new JTable(model);
		loadStudents();
		
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
				nameField.setText(model.getValueAt(row, 1).toString());
				emailField.setText(model.getValueAt(row, 2).toString());
				courseField.setText(model.getValueAt(row, 3).toString());
				semesterField.setText(model.getValueAt(row, 4).toString());
			}
		});
		
		JScrollPane tableScroll = new JScrollPane(table);
		
		JPanel centerWrap = new JPanel();
		centerWrap.setLayout(new BoxLayout(centerWrap, BoxLayout.Y_AXIS));
		centerWrap.add(formPanel);
		centerWrap.add(Box.createRigidArea(new Dimension(0, 5))); // 5px vertical gap
		centerWrap.add(buttonPanel);

		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		centerPanel.add(centerWrap);
		
		setLayout(new BorderLayout(10,10));
		add(searchWrapper, BorderLayout.NORTH);
		add(tableScroll, BorderLayout.SOUTH);
		add(centerPanel,BorderLayout.CENTER);
		
		setVisible(true);
		
	}
	
	public void loadStudents() {
		model.setRowCount(0);
		try {
			Connection con = DBConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM student");
			while(rs.next()) {
				model.addRow(new Object[] {
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("email"),
						rs.getString("course"),
						rs.getInt("semester")
				});
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void searchStudents() {
		model.setRowCount(0);
		String selectedField = searchFieldBox.getSelectedItem().toString().toLowerCase();
		String keyword = searchField.getText().trim();
		
		if (keyword.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Enter search keyword!");
	        return;
	    }
		
		try {
			Connection con = DBConnection.getConnection();
			String query;
			
			if(selectedField.equals("semester")) {
				query = "SELECT * FROM student WHERE semester = ?";
			}else{
				query = "SELECT * FROM student WHERE " + selectedField + " LIKE ?";
				keyword = "%" + keyword + "%";
			}
			
			PreparedStatement ps = con.prepareStatement(query);
			
			if(selectedField.equals("semester")) {
				try {
					ps.setInt(1, Integer.parseInt(keyword));
				}catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(this,"Please Enter a valid number");
					return;
				}
			}else {
				ps.setString(1,keyword);
			}
			
			ResultSet rs = ps.executeQuery();
			
			boolean found =false;
			while(rs.next()) {
				found = true;
				model.addRow(new Object[] {
						rs.getInt("id"),
						rs.getString("name"),
						rs.getString("email"),
						rs.getString("course"),
						rs.getInt("semester")
				});
			}
			if (!found) {
	            JOptionPane.showMessageDialog(this, "No records found.");
	        }
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String name = nameField.getText();
		String email = emailField.getText();
		String course = courseField.getText();
		int semester = semesterField.getText().isEmpty() ? 0 : Integer.parseInt(semesterField.getText());
		
		try {
			Connection con = DBConnection.getConnection();
			
			if(e.getSource() == addBtn) {
				String query = "INSERT INTO student(name, email, course, semester) VALUES(?,?,?,?) ";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setString(1, name);
				ps.setString(2, email);
				ps.setString(3, course);
				ps.setInt(4, semester);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Student Added!");
			}else if(e.getSource() == updateBtn && selectedId != -1) {
				String query = "UPDATE student SET name=?, email=?, course=?, semester=? WHERE id=?";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setString(1, name);
				ps.setString(2, email);
				ps.setString(3, course);
				ps.setInt(4, semester);
				ps.setInt(5, selectedId);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(this, "Student Updated!");
			}else if(e.getSource() == deleteBtn && selectedId != -1) {
				String query = "DELETE FROM student WHERE id =?";
				PreparedStatement ps = con.prepareStatement(query);
				ps.setInt(1, selectedId);
				JOptionPane.showMessageDialog(this, "Student Deleted!");
			}else if(e.getSource() ==  backBtn) {
				dispose();
				new AdminDashboard();
				return;
			}else if(e.getSource() == searchBtn) {
				searchStudents();
			}else if(e.getSource() == resetBtn) {
				searchField.setText("");
				clearForm();
				loadStudents();
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clearForm() {
		nameField.setText("");
		emailField.setText("");
		courseField.setText("");
		semesterField.setText("");
		selectedId = -1;
	}
}
