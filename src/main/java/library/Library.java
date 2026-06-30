package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {

    private static final double FINE_PER_DAY   = 0.50; // £0.50 per overdue day
    private static final int    MAX_BORROW_LIMIT = 5;
    private static final int    LOAN_WEEKS       = 2;

    private List<Book>   books;
    private List<Member> members;
    private FileHandler  fileHandler;

    public Library() {
        this.fileHandler = new FileHandler();
        this.books   = new ArrayList<>();
        this.members = new ArrayList<>();
        loadData();
    }

    // ----------------------------------------------------------------- Setup

    private void loadData() {
        books   = fileHandler.loadBooks();
        members = fileHandler.loadMembers();
        System.out.println("Data loaded: " + books.size() + " book(s), " + members.size() + " member(s).");
    }

    // ----------------------------------------------------------------- Books

    public boolean addBook(String isbn, String title, String author, int year) {
        if (isbn.isBlank() || title.isBlank() || author.isBlank()) {
            System.out.println("Error: ISBN, title and author cannot be empty.");
            return false;
        }
        if (findBookByIsbn(isbn) != null) {
            System.out.println("Error: A book with ISBN " + isbn + " already exists.");
            return false;
        }
        Book book = new Book(isbn, title, author, year);
        books.add(book);
        fileHandler.saveBooks(books);
        System.out.println("Book added: " + title);
        return true;
    }

    public boolean removeBook(String isbn) {
        Book book = findBookByIsbn(isbn);
        if (book == null) {
            System.out.println("Book not found with ISBN: " + isbn);
            return false;
        }
        if (!book.isAvailable()) {
            System.out.println("Cannot remove a book that is currently borrowed.");
            return false;
        }
        books.remove(book);
        fileHandler.saveBooks(books);
        System.out.println("Book removed: " + book.getTitle());
        return true;
    }

    public Book findBookByIsbn(String isbn) {
        return books.stream()
            .filter(b -> b.getIsbn().equalsIgnoreCase(isbn.trim()))
            .findFirst()
            .orElse(null);
    }

    public List<Book> searchBooks(String keyword) {
        String kw = keyword.toLowerCase().trim();
        return books.stream()
            .filter(b -> b.getTitle().toLowerCase().contains(kw)
                      || b.getAuthor().toLowerCase().contains(kw)
                      || b.getIsbn().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }
        printDivider("ALL BOOKS (" + books.size() + " total)");
        for (int i = 0; i < books.size(); i++) {
            System.out.printf("  %3d. %s%n", i + 1, books.get(i));
        }
    }

    public void displayAvailableBooks() {
        List<Book> available = books.stream().filter(Book::isAvailable).collect(Collectors.toList());
        if (available.isEmpty()) {
            System.out.println("No books currently available.");
            return;
        }
        printDivider("AVAILABLE BOOKS (" + available.size() + ")");
        for (int i = 0; i < available.size(); i++) {
            System.out.printf("  %3d. %s%n", i + 1, available.get(i));
        }
    }

    // --------------------------------------------------------------- Members

    public boolean registerMember(String id, String name, String email, String phone) {
        if (id.isBlank() || name.isBlank()) {
            System.out.println("Error: ID and name cannot be empty.");
            return false;
        }
        if (findMemberById(id) != null) {
            System.out.println("Error: Member with ID " + id + " already exists.");
            return false;
        }
        Member member = new Member(id, name, email, phone);
        members.add(member);
        fileHandler.saveMembers(members);
        System.out.println("Member registered: " + name + " (ID: " + id + ")");
        return true;
    }

    public Member findMemberById(String id) {
        return members.stream()
            .filter(m -> m.getId().equalsIgnoreCase(id.trim()))
            .findFirst()
            .orElse(null);
    }

    public void displayAllMembers() {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        printDivider("REGISTERED MEMBERS (" + members.size() + " total)");
        for (int i = 0; i < members.size(); i++) {
            System.out.printf("  %3d. %s%n", i + 1, members.get(i));
        }
    }

    // --------------------------------------------------------- Borrow/Return

    public boolean borrowBook(String isbn, String memberId) {
        Book   book   = findBookByIsbn(isbn);
        Member member = findMemberById(memberId);

        if (book == null)   { System.out.println("Error: Book not found (ISBN: " + isbn + ")."); return false; }
        if (member == null) { System.out.println("Error: Member not found (ID: " + memberId + ")."); return false; }
        if (!book.isAvailable()) {
            System.out.println("Error: Book is already borrowed. Due: " + book.getDueDate());
            return false;
        }
        if (member.getBorrowedCount() >= MAX_BORROW_LIMIT) {
            System.out.println("Error: Member has reached the maximum borrow limit (" + MAX_BORROW_LIMIT + " books).");
            return false;
        }

        book.setAvailable(false);
        book.setBorrowedBy(memberId);
        book.setDueDate(LocalDate.now().plusWeeks(LOAN_WEEKS));
        member.borrowBook(isbn);

        fileHandler.saveBooks(books);
        fileHandler.saveMembers(members);

        System.out.println("Book borrowed successfully!");
        System.out.printf("  Title   : %s%n", book.getTitle());
        System.out.printf("  Borrower: %s (%s)%n", member.getName(), memberId);
        System.out.printf("  Due Date: %s%n", book.getDueDate());
        return true;
    }

    public boolean returnBook(String isbn, String memberId) {
        Book   book   = findBookByIsbn(isbn);
        Member member = findMemberById(memberId);

        if (book == null)   { System.out.println("Error: Book not found."); return false; }
        if (member == null) { System.out.println("Error: Member not found."); return false; }
        if (book.isAvailable()) { System.out.println("Error: This book is not currently borrowed."); return false; }
        if (!book.getBorrowedBy().equalsIgnoreCase(memberId)) {
            System.out.println("Error: This book was not borrowed by member " + memberId + ".");
            return false;
        }

        boolean wasOverdue = book.isOverdue();
        long    daysLate   = book.daysOverdue();
        double  fine       = daysLate * FINE_PER_DAY;

        book.setAvailable(true);
        book.setBorrowedBy(null);
        book.setDueDate(null);
        member.returnBook(isbn);

        fileHandler.saveBooks(books);
        fileHandler.saveMembers(members);

        System.out.println("Book returned successfully!");
        System.out.printf("  Title: %s%n", book.getTitle());
        if (wasOverdue) {
            System.out.printf("  OVERDUE: %d day(s) | Fine: £%.2f%n", daysLate, fine);
        } else {
            System.out.println("  Returned on time. No fine.");
        }
        return true;
    }

    // ----------------------------------------------------------- Reservation

    public void displayBorrowedByMember(String memberId) {
        Member member = findMemberById(memberId);
        if (member == null) { System.out.println("Member not found."); return; }
        List<String> isbns = member.getBorrowedBooks();
        if (isbns.isEmpty()) {
            System.out.println(member.getName() + " has no borrowed books.");
            return;
        }
        printDivider("BOOKS BORROWED BY " + member.getName());
        for (String isbn : isbns) {
            Book b = findBookByIsbn(isbn);
            if (b != null) System.out.println("  " + b);
        }
    }

    // --------------------------------------------------------------- Reports

    public void displayStatistics() {
        long available = books.stream().filter(Book::isAvailable).count();
        long borrowed  = books.size() - available;
        long overdue   = books.stream().filter(b -> !b.isAvailable() && b.isOverdue()).count();

        printDivider("LIBRARY STATISTICS");
        System.out.printf("  Total Books      : %d%n", books.size());
        System.out.printf("  Available Books  : %d%n", available);
        System.out.printf("  Borrowed Books   : %d%n", borrowed);
        System.out.printf("  Overdue Books    : %d%n", overdue);
        System.out.printf("  Registered Members: %d%n", members.size());

        if (overdue > 0) {
            System.out.println();
            System.out.println("  OVERDUE DETAILS:");
            books.stream()
                .filter(b -> !b.isAvailable() && b.isOverdue())
                .forEach(b -> System.out.printf(
                    "    ISBN: %s | %s | %d day(s) overdue | Fine: £%.2f%n",
                    b.getIsbn(), b.getTitle(), b.daysOverdue(), b.daysOverdue() * FINE_PER_DAY));
        }
    }

    public void exportBooksToCSV() {
        fileHandler.exportBooksToCSV(books);
    }

    // ----------------------------------------------------------------- Utils

    private void printDivider(String title) {
        int width = 90;
        System.out.println();
        System.out.println("=".repeat(width));
        System.out.println("  " + title);
        System.out.println("=".repeat(width));
    }

    public int getBookCount()   { return books.size(); }
    public int getMemberCount() { return members.size(); }
}
