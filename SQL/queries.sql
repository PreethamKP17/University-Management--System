create database university;

use university;

create table users(
	id int primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(50) not null,
    role enum('admin','student','facuty') not null
);

ALTER TABLE users MODIFY role ENUM('admin','student','faculty') NOT NULL;

INSERT INTO users (username, password, role) VALUES ('admin1', 'adminpass', 'admin');

create table student(
	id int primary key auto_increment,
    name varchar(50),
    email varchar(50),
    course varchar(50),
    semester int
);

ALTER TABLE student ADD COLUMN password VARCHAR(255);
ALTER TABLE student ADD COLUMN role VARCHAR(20);
ALTER TABLE student ADD photo VARCHAR(255);
SET SQL_SAFE_UPDATES = 1;
UPDATE student SET role = 'student';

create table faculty(
	id int primary key auto_increment,
    name varchar(50),
    email varchar(50),
    department varchar(50),
    designation varchar(50)
);

ALTER TABLE faculty ADD COLUMN password VARCHAR(255);
ALTER TABLE faculty ADD COLUMN role VARCHAR(20);
UPDATE faculty SET role = 'faculty' WHERE id>0;
ALTER TABLE faculty ADD photo VARCHAR(255);

CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject_id INT,
    date DATE,
    status VARCHAR(10),
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);


CREATE TABLE marks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    subject VARCHAR(100),
    marks INT,
    max_marks INT,
    semester INT,
    FOREIGN KEY (student_id) REFERENCES student(id)
);

create table fees(
	id int primary key auto_increment,
    student_id int,
    amount decimal(10,2),
    status ENUM("Paid","Pending") default "Pending",
    date date,
    foreign key(student_id) references student(id)
);

CREATE TABLE subjects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    subject_code VARCHAR(50),
    subject_name VARCHAR(100),
    semester INT,
    faculty_id INT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id)
);



