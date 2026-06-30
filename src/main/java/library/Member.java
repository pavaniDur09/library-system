package library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Member implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String LIST_SEPARATOR = ",";

    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> borrowedBooks; // stores ISBNs

    public Member(String id, String name, String email, String phone) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.phone = phone;
        this.borrowedBooks = new ArrayList<>();
    }

    // Getters
    public String       getId()            { return id; }
    public String       getName()          { return name; }
    public String       getEmail()         { return email; }
    public String       getPhone()         { return phone; }
    public List<String> getBorrowedBooks() { return borrowedBooks; }

    // Setters
    public void setName(String name)   { this.name  = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public void borrowBook(String isbn)  { borrowedBooks.add(isbn); }
    public void returnBook(String isbn)  { borrowedBooks.remove(isbn); }
    public boolean hasBorrowedBook(String isbn) { return borrowedBooks.contains(isbn); }
    public int     getBorrowedCount()    { return borrowedBooks.size(); }

    /**
     * Serialise to pipe-delimited line for members.txt
     * id|name|email|phone|isbn1,isbn2,...
     */
    public String toFileLine() {
        String booksField = borrowedBooks.isEmpty() ? "none" : String.join(LIST_SEPARATOR, borrowedBooks);
        return String.join("|", id, name, email, phone, booksField);
    }

    /** Reconstruct a Member from a pipe-delimited line */
    public static Member fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) throw new IllegalArgumentException("Malformed member line: " + line);
        Member m = new Member(p[0], p[1], p[2], p[3]);
        if (!p[4].equals("none")) {
            for (String isbn : p[4].split(LIST_SEPARATOR)) {
                m.borrowBook(isbn.trim());
            }
        }
        return m;
    }

    @Override
    public String toString() {
        return String.format("ID: %-8s | Name: %-25s | Email: %-30s | Phone: %-15s | Books Borrowed: %d",
            id, name, email, phone, borrowedBooks.size());
    }
}
