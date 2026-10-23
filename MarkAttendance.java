package com.university;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class MarkAttendance extends JFrame {

    JComboBox<SubjectItem> subjectBox;
    JButton loadBtn, saveBtn;
    JTable studentTable;
    DefaultTableModel model;
    int facultyId;

    public MarkAttendance(int facultyId) {
        this.facultyId = facultyId;

        setTitle("Mark Attendance");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Top Panel
        JPanel topPanel = new JPanel();
        subjectBox = new JComboBox<>();
        loadSubjects();
        topPanel.add(new JLabel("Select Subject: "));
        topPanel.add(subjectBox);

        loadBtn = new JButton("Load Students");
        topPanel.add(loadBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Present?"}, 0) {
            public Class<?> getColumnClass(int column) {
                return (column == 2) ? Boolean.class : String.class;
            }

            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        studentTable = new JTable(model);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Save Button
        saveBtn = new JButton("Save Attendance");
        add(saveBtn, BorderLayout.SOUTH);

        // Listeners
        loadBtn.addActionListener(e -> loadStudents());
        saveBtn.addActionListener(e -> saveAttendance());

        setVisible(true);
    }
    
    private void loadSubjects() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT id, subject_name FROM subjects WHERE faculty_id = ?");
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                subjectBox.addItem(new SubjectItem(rs.getInt("id"), rs.getString("subject_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    private void loadStudents() {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            SubjectItem selectedSubject = (SubjectItem) subjectBox.getSelectedItem();
            if (selectedSubject == null) return;

            PreparedStatement ps = con.prepareStatement(
            	    "SELECT s.id, s.name FROM student s WHERE s.semester = (SELECT semester FROM subjects WHERE id = ?)"
            	);

            ps.setInt(1, selectedSubject.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), false});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void saveAttendance() {
        SubjectItem subject = (SubjectItem) subjectBox.getSelectedItem();
        if (subject == null) return;

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        try {
            Connection con = DBConnection.getConnection();

            for (int i = 0; i < model.getRowCount(); i++) {
                int studentId = Integer.parseInt(model.getValueAt(i, 0).toString());
                boolean present = (Boolean) model.getValueAt(i, 2);

                // Avoid duplicate for today
                PreparedStatement checkStmt = con.prepareStatement(
                    "SELECT id FROM attendance WHERE student_id = ? AND subject_id = ? AND date = ?");
                checkStmt.setInt(1, studentId);
                checkStmt.setInt(2, subject.id);
                checkStmt.setDate(3, today);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                	System.out.println("Attendance already marked for studentId: " + studentId);
                	continue; // already marked
                }

                PreparedStatement insertStmt = con.prepareStatement(
                    "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?)");
                insertStmt.setInt(1, studentId);
                insertStmt.setInt(2, subject.id);
                insertStmt.setDate(3, today);
                insertStmt.setString(4, present ? "Present" : "Absent");
                insertStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Helper class for ComboBox to store subject id + name
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
