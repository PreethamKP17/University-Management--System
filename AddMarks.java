package com.university;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class AddMarks extends JFrame{

	JComboBox<SubjectItem> subjectBox;
    JButton loadBtn, saveBtn;
    JTable marksTable;
    DefaultTableModel model;
    int facultyId;
	
	public AddMarks(int facultyId) {
		this.facultyId = facultyId;
		
		setTitle("Add/Update Marks");
		setSize(700,500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		//Top Panel
		JPanel topPanel = new JPanel();
		subjectBox = new JComboBox<>();
		loadSubjects();
		topPanel.add(new JLabel("Select Subject : "));
		topPanel.add(subjectBox);
		
		loadBtn = new JButton("Load Students");
		topPanel.add(loadBtn);
		
		add(topPanel, BorderLayout.NORTH);
		
		//Table
		model = new DefaultTableModel(new String[]{"Student ID", "Name","Semester","Marks","Max Marks"},0);
		marksTable = new JTable(model) {
			public boolean isCellEditable(int row, int column) {
				return column >= 3;
			}
		};
		
		add(new JScrollPane(marksTable), BorderLayout.CENTER);
		
		//Save Button
		saveBtn = new JButton("Save Marks");
		add(saveBtn, BorderLayout.SOUTH);
		
		loadBtn.addActionListener(e -> loadStudentsForSubject());
		saveBtn.addActionListener(e -> saveMarks());
		
		setVisible(true);
	}
	
	private void loadSubjects() {
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT id,subject_name FROM subjects WHERE faculty_id = ?");
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                subjectBox.addItem(new SubjectItem(rs.getInt("id"),rs.getString("subject_name")));
            }
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadStudentsForSubject() {
		model.setRowCount(0);
		SubjectItem selectedSubject = (SubjectItem) subjectBox.getSelectedItem();
	    if (selectedSubject == null) return;
		
		try{
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(
		            "SELECT s.id, s.name, s.semester " +
		            "FROM student s " +
		            "WHERE s.semester = (SELECT semester FROM subjects WHERE id = ?)"
		        );
			ps.setInt(1, selectedSubject.id);
	        ResultSet rs = ps.executeQuery();
	        
	        while(rs.next()) {
	        	model.addRow(new Object[] {
	        			rs.getInt("id"),
	        			rs.getString("name"),
	        			rs.getInt("semester"),
	        			"","",
	        	});
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void saveMarks() {
		SubjectItem subject = (SubjectItem) subjectBox.getSelectedItem();
		
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(
				    "INSERT INTO marks (student_id, subject, semester, marks, max_marks) VALUES (?, ?, ?, ?, ?) " +
				    "ON DUPLICATE KEY UPDATE marks = VALUES(marks), max_marks = VALUES(max_marks)"
				);
			
			for(int i=0; i<model.getRowCount();i++) {
				
				int studentId = Integer.parseInt(model.getValueAt(i, 0).toString());
				int semester = Integer.parseInt(model.getValueAt(i, 2).toString());
                String marksStr = model.getValueAt(i, 3).toString();
                String maxMarksStr = model.getValueAt(i, 4).toString();
                
                if(marksStr.isEmpty() || maxMarksStr.isEmpty()) continue;
                
                int marks = Integer.parseInt(marksStr);
                int maxMarks = Integer.parseInt(maxMarksStr);
                
                ps.setInt(1, studentId);
                ps.setInt(2, subject.id);
                ps.setInt(3, semester);
                ps.setInt(4, marks);
                ps.setInt(5, maxMarks);
                ps.addBatch();
			}
			
			ps.executeBatch();
			JOptionPane.showMessageDialog(this,"Marks saved successfully!");
			dispose();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	class SubjectItem {
	    int id;
	    String name;

	    SubjectItem(int id, String name) {
	        this.id = id;
	        this.name = name;
	    }

	    public String toString() {
	        return name;
	    }
	}

}
