# 📚 Library Management System

A console-based Library Management System built in Java as part of **Week 3 – Java Programming Basics**. The project demonstrates core Object-Oriented Programming principles, file I/O persistence, Java Collections, and clean console UI design.

---

## 🗂️ Project Structure

```
week3-library-system/
├── src/
│   └── main/
│       └── java/
│           └── library/
│               ├── Main.java          ← Entry point & console menu
│               ├── Book.java          ← Book entity class
│               ├── Member.java        ← Member entity class
│               ├── Library.java       ← Core business logic
│               └── FileHandler.java   ← File I/O (read/write/CSV export)
├── data/
│   ├── books.txt                      ← Persistent book storage
│   └── members.txt                    ← Persistent member storage
├── pom.xml                            ← Maven build file
├── .gitignore
└── README.md
```

---

## ✨ Features

| Feature | Description |
|---|---|
| **Add / Remove Books** | Add books with ISBN, title, author and year; remove books that are not borrowed |
| **Register Members** | Register library members with ID, name, email and phone |
| **Borrow a Book** | Borrow an available book; automatically sets a 2-week due date |
| **Return a Book** | Return a book and calculate any overdue fine (£0.50/day) |
| **Search Books** | Search across title, author, or ISBN (case-insensitive) |
| **View All Books / Members** | Display full formatted lists |
| **Member Borrowings** | View all books currently borrowed by a specific member |
| **Library Statistics** | Totals for books, availability, overdue items, and members |
| **CSV Export** | Export full book catalogue to `data/books_export.csv` |
| **Data Persistence** | All data automatically saved to and loaded from text files |
| **Input Validation** | All user input validated with helpful error messages |
| **Borrow Limit** | Members cannot borrow more than 5 books at once |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+** (for the Maven build path)  
  _Or_ just a plain `javac` compiler if you prefer to compile manually.

### Option A – Compile & Run with Maven

```bash
# 1. Clone or download the project
git clone https://github.com/YOUR_USERNAME/week3-library-system.git
cd week3-library-system

# 2. Build the project
mvn clean package

# 3. Run the JAR
java -jar target/library-system.jar
```

### Option B – Compile & Run Manually (no Maven)

```bash
# 1. Navigate to the source directory
cd week3-library-system

# 2. Compile all Java files
javac -d out src/main/java/library/*.java

# 3. Run the program (the data/ folder must be in the working directory)
java -cp out library.Main
```

> **Note:** The `data/` folder must exist in the directory where you run the program. It is included in the repo with sample seed data.

---

## 🖥️ Sample Session

```
╔══════════════════════════════════════════════════════════════╗
║          LIBRARY MANAGEMENT SYSTEM  v1.0                    ║
╚══════════════════════════════════════════════════════════════╝

Data loaded: 5 book(s), 3 member(s).

─────────────────────────────────────────
  MAIN MENU
─────────────────────────────────────────
  1. Add New Book
  2. View All Books
  3. Search Books
  4. Register New Member
  5. View All Members
  6. Borrow Book
  7. Return Book
  8. View Member's Borrowed Books
  9. Library Statistics
 10. Export Books to CSV
─────────────────────────────────────────
Enter your choice: 9

==========================================================================================
  LIBRARY STATISTICS
==========================================================================================
  Total Books       : 5
  Available Books   : 4
  Borrowed Books    : 1
  Overdue Books     : 0
  Registered Members: 3
```

---

## 🏗️ OOP Design

### `Book.java`
Encapsulates all book data (ISBN, title, author, year, availability, borrower, due date). Contains logic to detect overdue status and calculate days overdue. Serialises/deserialises itself to/from a pipe-delimited file line.

### `Member.java`
Holds member details (ID, name, email, phone) and a list of currently borrowed ISBNs. Provides `borrowBook()` and `returnBook()` methods to maintain its own borrow list.

### `Library.java`
The service layer. Coordinates all operations between `Book` and `Member` objects. Enforces business rules (borrow limit, availability check, ownership check on return). Delegates all persistence to `FileHandler`.

### `FileHandler.java`
Handles all file I/O. Reads and writes `books.txt` and `members.txt` using buffered readers/writers. Exports books to CSV. Gracefully skips malformed lines with a warning rather than crashing.

### `Main.java`
Entry point and UI. Presents the console menu, reads and validates all user input, and delegates every action to `Library`.

---

## 📁 Data File Format

### `books.txt`
```
isbn|title|author|year|available|borrowedBy|dueDate
9780134685991|Effective Java|Joshua Bloch|2018|true|null|null
9781617294945|Spring in Action|Craig Walls|2020|false|MEM001|2025-07-15
```

### `members.txt`
```
id|name|email|phone|isbn1,isbn2,...
MEM001|Alice Johnson|alice@email.com|07700900001|9781617294945
MEM002|Bob Smith|bob@email.com|07700900002|none
```

---

## 📋 Business Rules

- Books are loaned for **2 weeks** from the borrow date.
- Overdue fine is **£0.50 per day** past the due date.
- A member can hold a maximum of **5 books** at once.
- A book cannot be removed from the system while it is borrowed.
- Book search is **case-insensitive** and matches title, author, or ISBN.

---

## 🔧 Possible Extensions

- Password-protected admin login
- GUI front-end using JavaFX or Swing
- Book reservation / waitlist system
- Email notifications for overdue books (JavaMail)
- Replace flat-file storage with SQLite (JDBC)

---

## 👤 Author

**[Your Name]**  
Week 3 Submission – Java Programming Basics  
[Your Course / Cohort Name]
