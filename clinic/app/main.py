from db import get_connection

def show_doctors(cursor):
    cursor.execute("SELECT doctor_id, name, specialization FROM doctor")
    doctors = cursor.fetchall()

    print("\nAvailable Doctors:\n")

    for doc in doctors:
        print(f"{doc[0]}. {doc[1]} - {doc[2]}")

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
    print("Select Your Health Companion!\n")
    show_doctors(cursor)
    print("\n")

    doc_id = input("Enter Doctor ID: ")
    pat_id = input("Enter your Patient ID (given during registration): ")

    app_date = input("Enter your desired appointment date: ")
    app_time = input("Enter the time: ")

    query = """
    INSERT INTO appointment (patient_id, doctor_id, appointment_date, appointment_time)
    VALUES (%s, %s, %s, %s)
    """
    try:
        cursor.execute(query, (pat_id, doc_id, app_date, app_time))
        conn.commit()

        print("Appointment Booked Successfully")

    except Exception as e:
        print("Could not book appointment")
        print("That time slot may already be taken")

def view_appointments(cursor):

    print("\n--- View Appointments ---\n")

    pat_id = input("Enter your Patient ID: ")

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

    print("\nYour Appointments:\n")

    for appt in appointments:
        print(f"{appt[0]} {appt[1]} with {appt[2]} ({appt[3]})")

    print()

def cancel_appointment(cursor, conn):
    print("\n--- Cancel Appointment ---")
    pat_id = input("Insert Your Patient ID: ")

    query = """
        SELECT appointment_id, appointment_date, appointment_time
        FROM appointment
        WHERE patient_id = %s
        ORDER BY appointment_date, appointment_time
    """
    cursor.execute(query,(pat_id,))
    appointments = cursor.fetchall()

    if not appointments:
        print("No Appointments Found")
        return

    print("Your Appointments: ")
    for i, appt in enumerate(appointments, start=1):
        print(f"{i}. {appt[1]} {appt[2]}")

    choice = int(input("Select the appointment to cancle: "))
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

        print("\n====== Medical Appointment System ======")
        print("1. Register Patient")
        print("2. View Doctors")
        print("3. Book Appointment")
        print("4. View Appointment")
        print("5. Cancel Appointment")
        print("6. Exit")

        choice = input("\nEnter choice: ")

        if choice == "1":
            register_patient(cursor, conn)

        elif choice == "2":
            show_doctors(cursor)

        elif choice == "3":
            book_appointment(cursor,conn)

        elif choice == "4":
            view_appointments(cursor)

        elif choice == "5":
            cancel_appointment(cursor, conn)

        elif choice == "6":
            print("Goodbye.")
            break

        else:
            print("Invalid choice.")

if __name__ == "__main__":
    main()
