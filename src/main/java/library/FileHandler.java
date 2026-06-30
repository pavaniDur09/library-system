package library;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String DATA_DIR    = "data";
    private static final String BOOKS_FILE  = DATA_DIR + "/books.txt";
    private static final String MEMBERS_FILE= DATA_DIR + "/members.txt";
    private static final String CSV_FILE    = DATA_DIR + "/books_export.csv";

    public FileHandler() {
        // Ensure data directory exists
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ Books

    public void saveBooks(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                pw.println(b.toFileLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (!file.exists()) return books;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    books.add(Book.fromFileLine(line));
                } catch (Exception e) {
                    System.err.println("Skipping malformed book record at line " + lineNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    // --------------------------------------------------------------- Members

    public void saveMembers(List<Member> members) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members) {
                pw.println(m.toFileLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    public List<Member> loadMembers() {
        List<Member> members = new ArrayList<>();
        File file = new File(MEMBERS_FILE);
        if (!file.exists()) return members;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    members.add(Member.fromFileLine(line));
                } catch (Exception e) {
                    System.err.println("Skipping malformed member record at line " + lineNum + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading members: " + e.getMessage());
        }
        return members;
    }

    // ------------------------------------------------------------------- CSV

    public void exportBooksToCSV(List<Book> books) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("ISBN,Title,Author,Year,Available,BorrowedBy,DueDate");
            for (Book b : books) {
                pw.printf("\"%s\",\"%s\",\"%s\",%d,%s,\"%s\",\"%s\"%n",
                    b.getIsbn(),
                    b.getTitle().replace("\"", "\"\""),
                    b.getAuthor().replace("\"", "\"\""),
                    b.getYear(),
                    b.isAvailable(),
                    b.getBorrowedBy() == null ? "" : b.getBorrowedBy(),
                    b.getDueDate()    == null ? "" : b.getDueDate().toString()
                );
            }
            System.out.println("Books exported to " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
        }
    }
}
