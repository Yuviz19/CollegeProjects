from db import get_connection
from datetime import datetime
from tabulate import tabulate

def get_valid_date():
    while True:
        date_input = input("Enter date (YYYY-MM-DD): ")
        try:
            date_obj = datetime.strptime(date_input, "%Y-%m-%d")

            if date_obj.date() < datetime.today().date():
                print("Cannot book past dates.\n")
                continue

            return date_input

        except:
            print("Invalid date format. Use YYYY-MM-DD.\n")

def get_valid_time():
    while True:
        time_input = input("Enter time (HH:MM): ")
        try:
            datetime.strptime(time_input, "%H:%M")
            return time_input
        except:
            print("Invalid time format. Use HH:MM.\n")

def doctor_exists(cursor, doc_id):
    query = "SELECT doctor_id FROM doctor WHERE doctor_id = %s"
    cursor.execute(query, (doc_id,))
    return cursor.fetchone() is not None

def get_valid_docid(cursor):
    while True:
        try:
            doc_id = int(input("Enter Doctor ID: "))
            if not doctor_exists(cursor, doc_id):
                print("Invalid Doctor ID\n")
                continue
            return doc_id
        except:
            print("Invalid input. Enter a number.\n")

def patient_exists(cursor, pat_id):
    query = "SELECT patient_id FROM patient WHERE patient_id = %s"
    cursor.execute(query, (pat_id,))
    return cursor.fetchone() is not None

def get_patid(cursor):
    while True:
        try:
            pat_id = int(input("Enter Your Patient ID: "))
            if not patient_exists(cursor, pat_id):
                print("Invalid Patient ID. Please register first.\n")
                continue
            return pat_id
        except:
            print("Invalid input. Enter a number.\n")

def show_doctors(cursor):
    cursor.execute("SELECT doctor_id, name, specialization FROM doctor")
    doctors = cursor.fetchall()

    headers = ["ID", "Name", "Specialization"]

    print("\nAvailable Doctors:\n")
    print(tabulate(doctors, headers=headers, tablefmt="fancy_grid"))

def view_appointments(cursor):
    print("\n--- View Appointments ---\n")

    pat_id = get_patid(cursor)

    query = """
    SELECT a.appointment_date, a.appointment_time, d.name, d.specialization
    FROM appointment a
    JOIN doctor d ON a.doctor_id = d.doctor_id
    WHERE a.patient_id = %s
    ORDER BY a.appointment_date, a.appointment_time
    """

    cursor.execute(query, (pat_id,))
    appointments = cursor.fetchall()

    if not appointments:
        print("No appointments found.\n")
        return

    headers = ["Date", "Time", "Doctor", "Specialization"]

    print("\nYour Appointments:\n")
    print(tabulate(appointments, headers=headers, tablefmt="fancy_grid"))

def register_patient(cursor, conn):
    print("\n--- Patient Registration ---")

    name = input("Enter name: ")
    phone = input("Enter phone: ")
    email = input("Enter email: ")
    age = input("Enter age: ")

    query = """
    INSERT INTO patient (name, phone, email, age)
    VALUES (%s, %s, %s, %s)
    """

    cursor.execute(query, (name, phone, email, age))
    conn.commit()

    pat_id = cursor.lastrowid

    print("\nRegistration successful.\n")
    print(f"Your Patient ID is {pat_id}")

def book_appointment(cursor, conn):
    print("\n--- Book Appointment ---\n")

    show_doctors(cursor)

    doc_id = get_valid_docid(cursor)
    pat_id = get_patid(cursor)

    app_date = get_valid_date()
    app_time = get_valid_time()

    query = """
    INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time)
    VALUES (%s, %s, %s, %s)
    """

    try:
        cursor.execute(query, (pat_id, doc_id, app_date, app_time))
        conn.commit()
        print("\nAppointment Booked Successfully\n")

    except Exception:
        print("\nCould not book appointment.")
        print("That time slot may already be taken.\n")

def cancel_appointment(cursor, conn):
    print("\n--- Cancel Appointment ---\n")

    pat_id = get_patid(cursor)

    query = """
    SELECT appointment_id, appointment_date, appointment_time
    FROM appointment
    WHERE patient_id = %s
    ORDER BY appointment_date, appointment_time
    """

    cursor.execute(query, (pat_id,))
    appointments = cursor.fetchall()

    if not appointments:
        print("No appointments found.\n")
        return

    table = [(i, appt[1], appt[2]) for i, appt in enumerate(appointments, start=1)]
    headers = ["Choice", "Date", "Time"]

    print(tabulate(table, headers=headers, tablefmt="fancy_grid"))

    try:
        choice = int(input("\nSelect appointment to cancel: "))

        if choice < 1 or choice > len(appointments):
            print("Invalid selection\n")
            return

    except Exception:
        print("Invalid input\n")
        return

    selected = appointments[choice - 1]
    appointment_id = selected[0]

    delete_query = "DELETE FROM appointment WHERE appointment_id = %s"

    cursor.execute(delete_query, (appointment_id,))
    conn.commit()

    print("\nAppointment cancelled successfully.\n")

def main():
    conn = get_connection()
    cursor = conn.cursor()

    while True:
        print("\n" + "="*45)
        print("     MEDICAL APPOINTMENT SYSTEM")
        print("="*45)

        print("1. Register Patient")
        print("2. View Doctors")
        print("3. Book Appointment")
        print("4. View Appointments")
        print("5. Cancel Appointment")
        print("6. Exit")

        choice = input("\nEnter choice: ")

        if choice == "1":
            register_patient(cursor, conn)

        elif choice == "2":
            show_doctors(cursor)

        elif choice == "3":
            book_appointment(cursor, conn)

        elif choice == "4":
            view_appointments(cursor)

        elif choice == "5":
            cancel_appointment(cursor, conn)

        elif choice == "6":
            print("Goodbye.")
            break

        else:
            print("Invalid choice.\n")

if __name__ == "__main__":
    main()
