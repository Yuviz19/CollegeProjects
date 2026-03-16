DROP DATABASE IF EXISTS clinic;
CREATE DATABASE clinic;
USE clinic;

CREATE TABLE doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    specialization VARCHAR(100)
);

INSERT INTO doctor (name, specialization) VALUES
('Dr Sharma','Cardiology'),
('Dr Patel','Dermatology'),
('Dr Khan','Orthopedics');

CREATE TABLE patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    age INT
);

CREATE TABLE appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT,
    doctor_id INT,
    appointment_date DATE,
    appointment_time TIME,
    status VARCHAR(20),

    FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(doctor_id),

    UNIQUE (doctor_id, appointment_date, appointment_time)
);
