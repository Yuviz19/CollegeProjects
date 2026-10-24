# Digital Certificate Issuance and Verification System

A simple Java desktop application to **issue, view, verify and revoke digital certificates**.
Built with plain **Java Swing** (GUI), **JDBC + MySQL** (database) and **threads**
(SwingWorker + a background monitor thread).

---

## 1. What you need

| Requirement | Notes |
|---|---|
| JDK 8 or newer | `java` and `javac` on the terminal (`java -version` to check) |
| MySQL Server 8+ | running as a service |
| The two jars | `lib/mysql-connector-j.jar`, `lib/flatlaf.jar` (already included) |

---

## 2. Database setup (one time)

### Step 1 — Log in to MySQL

```bash
mysql -u root -p
```

> Forgot the root password? See **Troubleshooting** below.

### Step 2 — Run the setup script

From the project folder:

```bash
mysql -u root -p < database.sql
```

This creates the database `digiCertJAVA_db`, the tables `users` and
`certificates`, and the login: **admin / admin123**.

> Running it twice only prints a harmless "Duplicate entry 'admin'" error.

### Step 3 — Put your MySQL password in the app

Open `src/db/DB.java` and edit the five constants near the top:

```java
private static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DB_NAME  = "digiCertJAVA_db";
private static final String USER     = "root";
private static final String PASSWORD = "your-mysql-password";  // <-- edit this
```

---

## 3. Run the app

### Linux / macOS

```bash
./run.sh
```

This compiles everything (`javac`) and starts the app.
The script also exports `_JAVA_AWT_WM_NONREPARENTING=1`, which is **required**
on sway / i3 / dwm window managers — without it Java windows show up blank white.

### Windows

Double-click `run.bat`.

### Log in

| Username | Password |
|---|---|
| `admin` | `admin123` |

On first start, four sample certificates are added automatically
(one each: normal, expiring soon, expired, revoked) so every screen has something to show.

---

## 4. Using the app (step by step)

1. **Issue Certificate** — fill the form (holder name is required; dates must be `yyyy-MM-dd`).
   On submit you get a dialog with a **Certificate ID** like `DC-1A2B3C4D`. Keep it —
   it is the key used for verification.
2. **View / Search** — a table of all certificates. Type any ID, name or course and press
   *Search*. Colors: light red = revoked, grey = expired.
3. **Verify** — enter a Certificate ID. The app re-hashes the stored details and compares
   with the signature saved at issue time:
   - **VALID** (green) — authentic and active
   - **TAMPERED** (red) — someone edited the record directly in the database
   - **REVOKED** (red) — authentic but withdrawn
   - **EXPIRED** (amber) — authentic but past the expiry date
   - **NOT FOUND** — no such ID
4. **Revoke** — pick an ACTIVE certificate from the dropdown, type a reason and confirm.
   It can no longer be issued as valid, and Verify will report REVOKED.

Tip to demo the "tampered" check: change the holder name of one row directly in
MySQL (`UPDATE certificates SET holder_name='X' WHERE id='...'`) and then verify
with the same ID — you will get TAMPERED.

---

## 5. How the course requirements are implemented

| Requirement | Where | How |
|---|---|---|
| Java Swing | `src/ui/` | Frames, panels, JTable, dialogs; layout with BoxLayout / GridBagLayout |
| JDBC | `src/db/DB.java`, `src/db/CertificateDAO.java` | Plain `Connection` + `PreparedStatement` queries against MySQL |
| Threads | every panel + `src/util/ExpiryMonitor.java` | ① `SwingWorker` — every database operation runs on a background worker thread so the UI never freezes (e.g. `IssuePanel.onIssue()`). ② `ExpiryMonitor` — a daemon `Thread` that runs every 60 s, counts expiring/expired certificates and updates the status bar via `SwingUtilities.invokeLater`. |

Signature idea (why a certificate can be "tampered"): when issued, the app hashes
`holder|course|institution|grade|issueDate|expiryDate` with **SHA-256** and stores it as the
`signature` column. Verification recomputes that hash from the stored details and compares.

---

## 6. Project structure

```
digiCertJAVA/
├── database.sql                    setup script (create database, tables, admin login)
├── run.sh / run.bat                compile + start
├── lib/
│   ├── mysql-connector-j.jar       JDBC driver
│   └── flatlaf.jar                 modern look-and-feel (optional, silently skipped if missing)
└── src/
    ├── Main.java                   entry point: DB check, demo data, login window
    ├── model/Certificate.java      plain data object (one certificate)
    ├── db/DB.java                  MySQL connection constants + login check
    ├── db/CertificateDAO.java      all certificate queries (insert/find/search/revoke)
    ├── util/Util.java              SHA-256 hash + random ID generator
    ├── util/ExpiryMonitor.java     background thread (expiring countdown)
    └── ui/
        ├── Style.java              shared colors, fonts, buttons
        ├── LoginFrame.java         login window
        ├── MainFrame.java          sidebar + content area + status bar
        ├── IssuePanel.java         issue a certificate
        ├── ViewPanel.java          view / search table
        ├── VerifyPanel.java        verify by ID (with verdict)
        └── RevokePanel.java        revoke with reason
```

---

## 7. Troubleshooting

| Problem | Fix |
|---|---|
| **Blank white window on Linux** | Your window manager is sway/i3/dwm-type. Run via `./run.sh` (it sets `_JAVA_AWT_WM_NONREPARENTING=1`). If you start Java manually on the terminal, export that variable first. |
| **"Cannot reach MySQL" at startup** | MySQL service not running (`sudo systemctl start mysql`) or wrong `USER`/`PASSWORD` in `src/db/DB.java`. |
| `Access denied for user 'root'@'localhost'` | Wrong MySQL password in `DB.java` — or reset it (next row). |
| **Forgot the MySQL root password** | `sudo pkill mysqld` then `sudo -u mysql /usr/libexec/mysqld --skip-grant-tables &`, then `mysql -u root` and `ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpass'; FLUSH PRIVILEGES;`, then `sudo pkill mysqld` and `sudo systemctl start mysql`. |
| `Duplicate entry 'admin'` when running `database.sql` | Harmless — the admin user already exists. |
| `MySQL JDBC driver not found` | The jar `lib/mysql-connector-j.jar` is missing. |
| App runs but a panel shows "Database error" | Same MySQL checks as above; run from a terminal to read the full stack trace. |
