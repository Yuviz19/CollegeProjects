CREATE DATABASE IF NOT EXISTS clinic;
USE clinic;

-- using if not exits so that the data may not get lost
CREATE TABLE IF NOT EXISTS doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    specialization VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    age INT
);

CREATE TABLE IF NOT EXISTS appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    appointment_time TIME,

    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),

    UNIQUE (doctor_id, appointment_date, appointment_time)
);

-- insert igore so that that the doctos may not enter as duplicates
INSERT IGNORE INTO doctor (name, specialization) VALUES
('Dr Arjun Sharma','Cardiology'),
('Dr Neha Patel','Dermatology'),
('Dr Imran Khan','Orthopedics'),
('Dr Kavita Verma','Pediatrics'),
('Dr Rohit Mehta','Neurology'),
('Dr Ananya Rao','Gynecology'),
('Dr Vikram Singh','General Medicine'),
('Dr Sneha Kapoor','Endocrinology'),
('Dr Aditya Nair','Gastroenterology'),
('Dr Priya Iyer','Psychiatry'),
('Dr Karan Malhotra','Urology'),
('Dr Riya Chatterjee','Ophthalmology'),
('Dr Siddharth Gupta','ENT'),
('Dr Meera Joshi','Pulmonology'),
('Dr Rajesh Bansal','Nephrology'),
('Dr Tanvi Desai','Oncology'),
('Dr Aman Srivastava','Rheumatology'),
('Dr Pooja Sethi','Radiology'),
('Dr Nikhil Arora','Sports Medicine'),
('Dr Aditi Kulkarni','Family Medicine');

