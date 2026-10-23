package com.university;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class StudentDashboard extends JFrame {
	int studentId;
	JLabel nameLabel, emailLabel, courseLabel, photoLabel;
	JButton changePhotoBtn, logoutBtn;
	JTable marksTable, attendanceTable, feeTable;
	
	public StudentDashboard(int id) {
		
		this.studentId = id;
		
		setTitle("Student DashBoard");		 
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
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
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(photoLabel, BorderLayout.CENTER);
		leftPanel.add(changePhotoBtn, BorderLayout.SOUTH);
		
		//LogoutBtn
		logoutBtn = new JButton("Logout");
		logoutBtn.addActionListener(e ->{
			dispose();
			new Login();
		});
		JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		logoutPanel.add(logoutBtn);
		
		//Top panel
		JPanel infoPanel = new JPanel(new GridLayout(3,1));
		infoPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		nameLabel = new JLabel("Name : ");
		emailLabel = new JLabel("Email : ");
		courseLabel = new JLabel("Course : ");
		infoPanel.add(nameLabel);
		infoPanel.add(emailLabel);
		infoPanel.add(courseLabel);
		
		// Center Tabs for Marks & Attendance 
		JTabbedPane tabs = new JTabbedPane();
		
		//Marks Tab
		marksTable = new JTable();
		JScrollPane marksScroll = new JScrollPane(marksTable);
		tabs.add("Marks",marksScroll);
		
		attendanceTable = new JTable();
		JScrollPane attendanceScroll = new JScrollPane(attendanceTable);
		tabs.add("Attendance", attendanceScroll);
		
		//fee Table
		feeTable = new JTable();
		JScrollPane feeScroll = new JScrollPane(feeTable);
		tabs.add("Fees",feeScroll);
		
		profilePanel.add(leftPanel, BorderLayout.WEST);
		profilePanel.add(infoPanel, BorderLayout.CENTER);
		profilePanel.add(logoutPanel, BorderLayout.SOUTH );
		
		add(profilePanel, BorderLayout.NORTH);
		add(tabs, BorderLayout.CENTER);
		
		loadProfile();
		loadMarks();
		loadAttendance();
		loadFees();
		
		setVisible(true);
	}

	private void loadProfile() {
		try {
			Connection con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE id = ?");
			ps.setInt(1, studentId);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				nameLabel.setText("Name : "+rs.getString("name"));
				emailLabel.setText("Email : "+rs.getString("email"));
				courseLabel.setText("Course : "+rs.getString("course"));
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
	
	private void loadMarks() {
		DefaultTableModel model = new DefaultTableModel(new String[]{"Subject", "Marks", "Max Marks", "Semester"}, 0);
		try {
			Connection con = DBConnection.getConnection();
			 String query = "SELECT s.subject_name, m.marks, m.max_marks, m.semester " +
                     "FROM marks m " +
                     "JOIN subjects s ON m.subject = s.id " +
                     "WHERE m.student_id = ?";
	        PreparedStatement ps = con.prepareStatement(query);
	      
			ps.setInt(1, studentId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
			model.addRow(new Object[] {
					rs.getString("subject_name"),
					rs.getInt("marks"),
					rs.getInt("max_marks"),
					rs.getInt("semester")
				});
			}
			marksTable.setModel(model);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadAttendance() {
		 DefaultTableModel model = new DefaultTableModel(new String[]{"Subject", "Total", "Present", "Percentage"}, 0);
	        try {
	            Connection con = DBConnection.getConnection();
	            String query =
	                    "SELECT s.subject_name, " +
	                    "       COUNT(*) AS total_classes, " +
	                    "       SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present_classes " +
	                    "FROM attendance a " +
	                    "JOIN subjects s ON a.subject_id = s.id " +
	                    "WHERE a.student_id = ? " +
	                    "GROUP BY s.subject_name";
	            
	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setInt(1, studentId);
	            ResultSet rs = ps.executeQuery();
	            
	            while (rs.next()) {
	                int total = rs.getInt("total_classes");
	                int present = rs.getInt("present_classes");
	                double percent = (total > 0) ? (present * 100.0 / total) : 0;	                
	                
	                model.addRow(new Object[]{
	                    rs.getString("subject_name"),
		                total,
		                present,
		                String.format("%.2f%%", percent)
	              });
	           }
	            
	           attendanceTable.setModel(model);
	           
	       } catch (Exception e) {
	            e.printStackTrace();
	    }
	    
	}
	
	private void loadFees() {
	    DefaultTableModel model = new DefaultTableModel(new String[]{"Amount", "Status", "Date"}, 0);
	    
	    double totalPaid = 0;
	    double totalDue = 0;

	    try {
	        Connection con = DBConnection.getConnection();

	        // 1. Load fee records into the table
	        PreparedStatement ps1 = con.prepareStatement("SELECT amount, status, date FROM fees WHERE student_id = ?");
	        ps1.setInt(1, studentId);
	        ResultSet rs1 = ps1.executeQuery();
	        while (rs1.next()) {
	            double amount = rs1.getDouble("amount");
	            String status = rs1.getString("status");
	            model.addRow(new Object[] {
	                amount,
	                status,
	                rs1.getDate("date")
	            });

	            // Summarize while populating
	            if (status.equalsIgnoreCase("paid")) totalPaid += amount;
	            else totalDue += amount;
	        }
	        feeTable.setModel(model);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void changeProfilePicture() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select Profile Picture");
		int result = fileChooser.showOpenDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String imagePath = fileChooser.getSelectedFile().getAbsolutePath();
			
			try {
				Connection con = DBConnection.getConnection();
				PreparedStatement ps = con.prepareStatement("UPDATE student SET photo = ? WHERE id = ?");
				ps.setString(1, imagePath);
				ps.setInt(2, studentId);
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
}
