package com.university;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class FacultyDashboard extends JFrame{

    int facultyId;
    JLabel nameLabel, emailLabel, deptLabel, photoLabel;
    JTable subjectTable, attendanceTable, marksTable;
    JButton changePhotoBtn, markAttendanceBtn, addMarksBtn, logoutBtn;
    DefaultTableModel attendanceModel, marksModel;
    
    public FacultyDashboard(int facultyId) {
        this.facultyId = facultyId;

        setTitle("Faculty Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20,20,10,20));

		//Left Profile picture
		photoLabel = new JLabel();
		photoLabel.setPreferredSize(new Dimension(150,150));
		photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//Button to change profile
		changePhotoBtn = new JButton("Change Profile");
		changePhotoBtn.addActionListener(e -> changeProfilePicture());
		
		//Logout Button
		logoutBtn = new JButton("Logout");
		logoutBtn.addActionListener(e ->{
			dispose();
			new Login();
		});
		JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		logoutPanel.add(logoutBtn);
		
		//left Panel
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(photoLabel, BorderLayout.CENTER);
		leftPanel.add(changePhotoBtn, BorderLayout.SOUTH);
		
		//Top panel
		JPanel infoPanel = new JPanel(new GridLayout(3,1));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		nameLabel = new JLabel("Name : ");
		emailLabel = new JLabel("Email : ");
		deptLabel = new JLabel("Department : ");
		infoPanel.add(nameLabel);
		infoPanel.add(emailLabel);
		infoPanel.add(deptLabel);
		
		
		// Center Tabs 
		JTabbedPane tabs = new JTabbedPane();
		
		//Subject Tab
		subjectTable = new JTable();
		JScrollPane subjectScroll = new JScrollPane(subjectTable);
		tabs.add("My Subject",subjectScroll);
		
		markAttendanceBtn= new JButton("Mark Attendance");
		JPanel topAttendBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topAttendBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topAttendBtnPanel.add(markAttendanceBtn);
		markAttendanceBtn.addActionListener(e -> {
		    MarkAttendance markWin = new MarkAttendance(facultyId);
		    markWin.addWindowListener(new WindowAdapter() {
		        public void windowClosed(WindowEvent we) {
		            loadAttendanceData();
		        }
		    });
		});

		
		JPanel attendancePanel = new JPanel(new BorderLayout());
		attendancePanel.add(topAttendBtnPanel, BorderLayout.NORTH);
		
		attendanceTable = new JTable();
		attendanceModel = new DefaultTableModel(new String[]{"Student ID", "Subject", "Date", "Status","Present Classes", "Total Classes"}, 0);
		attendanceTable.setModel(attendanceModel);
		attendancePanel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
		
		tabs.add("Attendance", attendancePanel);
		
		//Marks Table
		addMarksBtn = new JButton("Add/Update Marks");
		JPanel topMarksBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topMarksBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		topMarksBtnPanel.add(addMarksBtn);
		
		addMarksBtn.addActionListener(e -> {
		    AddMarks addWin = new AddMarks(facultyId);
		    addWin.addWindowListener(new WindowAdapter() {
		        public void windowClosed(WindowEvent we) {
		            loadMarksData();
		        }
		    });
		});

		JPanel marksPanel = new JPanel(new BorderLayout());
		marksPanel.add(topMarksBtnPanel, BorderLayout.NORTH);
		
		marksTable = new JTable();
		marksModel = new DefaultTableModel(new String[]{"Student ID", "Subject", "Marks", "Max Marks", "Semester"}, 0);
		marksTable.setModel(marksModel);
		marksPanel.add(new JScrollPane(marksTable), BorderLayout.CENTER);
		
		tabs.add("Marks", marksPanel);
		
		profilePanel.add(leftPanel, BorderLayout.WEST);
		profilePanel.add(infoPanel, BorderLayout.CENTER);
		profilePanel.add(logoutPanel, BorderLayout.SOUTH);
		
		add(profilePanel, BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);
		
		loadProfile();
	    loadSubjects();
        
        setVisible(true);
        
        this.addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent e) {
                loadAttendanceData();
                loadMarksData();
            }
        });

        
    }
    
    private void loadProfile() {
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM faculty WHERE id = ?");
			ps.setInt(1, facultyId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				nameLabel.setText("Name : "+rs.getString("name"));
				emailLabel.setText("Email : "+rs.getString("email"));
				deptLabel.setText("Department : "+rs.getString("department"));
			}
			
			//Load Image
			String photoPath = rs.getString("photo");
			ImageIcon icon;
			if(photoPath !=null && !photoPath.trim().isEmpty()) {
				icon = new ImageIcon(photoPath);
			}else {
				icon = new ImageIcon("src/com/university/icons/defaultProfile.png");
			}
			
			Image image = icon.getImage().getScaledInstance(150,150,Image.SCALE_SMOOTH);
			icon = new ImageIcon(image);
			photoLabel.setIcon(icon);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private void changeProfilePicture() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Profile Picture");
		int result = fileChooser.showOpenDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
			
			try {
				Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("UPDATE faculty SET photo = ? WHERE id = ?");
				ps.setString(1, imagePath);
				ps.setInt(2, facultyId);
				int rowsUpdated = ps.executeUpdate();
				
				if(rowsUpdated > 0) {
					ImageIcon icon = new ImageIcon(imagePath);
					Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
					photoLabel.setIcon(new ImageIcon(img));
					JOptionPane.showMessageDialog(this, "Profile picture updated successful!");
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Failed to update profile picture");
			}
		}
		
	}

    private void loadSubjects() {
    	DefaultTableModel model = new DefaultTableModel(new String[]{"Subject Code", "Subject Name", "Semester"}, 0);
    	try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT subject_code, subject_name, semester FROM subjects WHERE faculty_id = ?");
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("subject_code"),
                        rs.getString("subject_name"),
                        rs.getInt("semester")
                });
            }
            subjectTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAttendanceData() {
        attendanceModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.student_id, s.subject_name, a.date, a.status, " +
                    " (SELECT COUNT(*) FROM attendance a2 WHERE a2.student_id = a.student_id AND a2.subject_id = a.subject_id AND a2.status = 'Present') AS present_classes, " +
                    " (SELECT COUNT(*) FROM attendance a2 WHERE a2.student_id = a.student_id AND a2.subject_id = a.subject_id) AS total_classes " +
                    "FROM attendance a " +
                    "JOIN subjects s ON a.subject_id = s.id " +
                    "WHERE s.faculty_id = ? " +
                    "ORDER BY a.date DESC"
                );
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	attendanceModel.addRow(new Object[]{
            	        rs.getInt("student_id"),
            	        rs.getString("subject_name"),
            	        rs.getDate("date"),
            	        rs.getString("status"),
            	        rs.getInt("present_classes"),
            	        rs.getInt("total_classes")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMarksData() {
        marksModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
            	    "SELECT m.student_id, s.subject_name, m.marks, m.max_marks, m.semester " +
            	    "FROM marks m " +
            	    "JOIN subjects s ON m.subject = s.id " +
            	    "WHERE s.faculty_id = ?"
            	);

            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                marksModel.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("subject_name"),
                    rs.getInt("marks"),
                    rs.getInt("max_marks"),
                    rs.getInt("semester")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
