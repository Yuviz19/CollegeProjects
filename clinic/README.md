# Medical Appointment System (CLI)

A terminal-based Medical Appointment System built using **Python** and **MySQL**, designed to simulate real-world appointment booking workflows with proper data validation and relational database design.

---

## Overview

This project allows patients to:

* Register themselves in the system
* View available doctors and their specializations
* Book appointments with doctors
* View their scheduled appointments
* Cancel appointments when needed

The system is designed with a focus on **data integrity, validation, and usability**, making it more than just a basic CRUD application.

---

## Key Features

* **Patient Registration** with auto-generated ID
* **Doctor Listing** with specialization
* **Appointment Booking System**
* **View Appointments (JOIN queries)**
* **Cancel Appointment functionality**
* **Input Validation Layer** (IDs, date, time)
* **Prevents invalid patient/doctor usage**
* **Structured CLI UI using tabular display**
* **Error handling for duplicate bookings**

---

## Tech Stack

* **Python** (Core Logic)
* **MySQL** (Database)
* **mysql-connector-python** (DB Connector)
* **tabulate** (CLI Table UI)

---

## Database Design

The system uses a relational schema with:

* `patient` table
* `doctor` table
* `appointment` table

Key concepts used:

* Primary Keys
* Foreign Keys
* Relational Mapping
* Basic Constraints

---

## How to Run

### 1. Clone the repository

```bash
git clone <your-repo-link>
cd clinic
```

---

### 2. Setup database

```bash
mysql -u root -p < sql/schema.sql
```

---

### 3. Install dependencies

If using `uv`:

```bash
uv pip install mysql-connector-python
uv pip install tabulate
```

---

### 4. Run the application

```bash
uv run python app/main.py
```

---

## Sample Flow

```text
1 → Register Patient
2 → View Doctors
3 → Book Appointment
4 → View Appointments
5 → Cancel Appointment
```

---

## Validation & Safety

* Ensures only **registered patients** can interact with the system
* Validates **doctor IDs** before booking
* Enforces proper **date (YYYY-MM-DD)** and **time (HH:MM)** formats
* Prevents invalid operations through controlled input loops

---

## Extra

The application can also be accessed remotely via **SSH**, allowing multi-device interaction through the terminal.

---

## Future Improvements

* Time-slot based booking system
* Session-based login (no repeated patient ID input)
* Better constraint handling before DB insertion
* Modular code structure

---

## Conclusion

This project demonstrates practical understanding of:

* Database design and relationships
* Backend logic with Python
* Input validation and error handling
* Building usable CLI applications

---

## Author

Yuvraj Singh
Engineering Student

---

