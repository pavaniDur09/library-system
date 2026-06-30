package library;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String isbn;
    private String title;
    private String author;
    private int year;
    private boolean available;
    private String borrowedBy;
    private LocalDate dueDate;

    public Book(String isbn, String title, String author, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.available = true;
        this.borrowedBy = null;
        this.dueDate = null;
    }

    // Getters
    public String getIsbn()       { return isbn; }
    public String getTitle()      { return title; }
    public String getAuthor()     { return author; }
    public int    getYear()       { return year; }
    public boolean isAvailable()  { return available; }
    public String getBorrowedBy() { return borrowedBy; }
    public LocalDate getDueDate() { return dueDate; }

    // Setters
    public void setAvailable(boolean available)   { this.available = available; }
    public void setBorrowedBy(String borrowedBy)  { this.borrowedBy = borrowedBy; }
    public void setDueDate(LocalDate dueDate)     { this.dueDate = dueDate; }

    public boolean isOverdue() {
        if (dueDate == null || available) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public long daysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    /**
     * Serialise to a single pipe-delimited line for books.txt
     * isbn|title|author|year|available|borrowedBy|dueDate
     */
    public String toFileLine() {
        return String.join("|",
            isbn,
            title,
            author,
            String.valueOf(year),
            String.valueOf(available),
            borrowedBy == null ? "null" : borrowedBy,
            dueDate   == null ? "null" : dueDate.format(DATE_FORMAT)
        );
    }

    /** Reconstruct a Book from a pipe-delimited line */
    public static Book fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 7) throw new IllegalArgumentException("Malformed book line: " + line);
        Book b = new Book(p[0], p[1], p[2], Integer.parseInt(p[3]));
        b.setAvailable(Boolean.parseBoolean(p[4]));
        b.setBorrowedBy(p[5].equals("null") ? null : p[5]);
        b.setDueDate(p[6].equals("null")    ? null : LocalDate.parse(p[6], DATE_FORMAT));
        return b;
    }

    @Override
    public String toString() {
        String status = available
            ? "Available"
            : "Borrowed by: " + borrowedBy + " | Due: " + dueDate
              + (isOverdue() ? " [OVERDUE " + daysOverdue() + " day(s)]" : "");
        return String.format("ISBN: %-15s | Title: %-35s | Author: %-20s | Year: %d | %s",
            isbn, title, author, year, status);
    }
}
