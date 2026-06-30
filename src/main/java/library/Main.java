package library;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Library library = new Library();
    private static final Scanner scanner  = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ", 1, 10);
            System.out.println();
            switch (choice) {
                case 1  -> menuAddBook();
                case 2  -> library.displayAllBooks();
                case 3  -> menuSearchBooks();
                case 4  -> menuRegisterMember();
                case 5  -> library.displayAllMembers();
                case 6  -> menuBorrowBook();
                case 7  -> menuReturnBook();
                case 8  -> menuMemberBorrowings();
                case 9  -> library.displayStatistics();
                case 10 -> {
                    System.out.println("Exporting books to CSV...");
                    library.exportBooksToCSV();
                }
                default -> System.out.println("Invalid option.");
            }

            System.out.println();
            System.out.print("Press ENTER to continue...");
            scanner.nextLine();
        }
    }

    // ----------------------------------------------------------------- Menus

    private static void menuAddBook() {
        System.out.println("=== ADD NEW BOOK ===");
        String isbn   = readString("ISBN         : ");
        String title  = readString("Title        : ");
        String author = readString("Author       : ");
        int    year   = readInt("Year (e.g. 2023): ", 1000, 2100);
        library.addBook(isbn, title, author, year);
    }

    private static void menuSearchBooks() {
        System.out.println("=== SEARCH BOOKS ===");
        String keyword = readString("Enter keyword (title / author / ISBN): ");
        List<Book> results = library.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No books found matching \"" + keyword + "\".");
        } else {
            System.out.println("Found " + results.size() + " result(s):");
            System.out.println("-".repeat(90));
            for (int i = 0; i < results.size(); i++) {
                System.out.printf("  %3d. %s%n", i + 1, results.get(i));
            }
        }
    }

    private static void menuRegisterMember() {
        System.out.println("=== REGISTER NEW MEMBER ===");
        String id    = readString("Member ID    : ");
        String name  = readString("Full Name    : ");
        String email = readString("Email        : ");
        String phone = readString("Phone        : ");
        library.registerMember(id, name, email, phone);
    }

    private static void menuBorrowBook() {
        System.out.println("=== BORROW BOOK ===");
        library.displayAvailableBooks();
        System.out.println();
        String isbn     = readString("Enter ISBN of book to borrow: ");
        String memberId = readString("Enter Member ID             : ");
        library.borrowBook(isbn, memberId);
    }

    private static void menuReturnBook() {
        System.out.println("=== RETURN BOOK ===");
        String isbn     = readString("Enter ISBN of book to return: ");
        String memberId = readString("Enter Member ID             : ");
        library.returnBook(isbn, memberId);
    }

    private static void menuMemberBorrowings() {
        System.out.println("=== VIEW MEMBER BORROWINGS ===");
        String memberId = readString("Enter Member ID: ");
        library.displayBorrowedByMember(memberId);
    }

    // --------------------------------------------------------------- Helpers

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          LIBRARY MANAGEMENT SYSTEM  v1.0                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("─────────────────────────────────────────");
        System.out.println("  MAIN MENU");
        System.out.println("─────────────────────────────────────────");
        System.out.println("  1. Add New Book");
        System.out.println("  2. View All Books");
        System.out.println("  3. Search Books");
        System.out.println("  4. Register New Member");
        System.out.println("  5. View All Members");
        System.out.println("  6. Borrow Book");
        System.out.println("  7. Return Book");
        System.out.println("  8. View Member's Borrowed Books");
        System.out.println("  9. Library Statistics");
        System.out.println(" 10. Export Books to CSV");
        System.out.println("─────────────────────────────────────────");
    }

    /**
     * Read a trimmed, non-empty string from the user.
     */
    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) System.out.println("  Input cannot be empty. Please try again.");
        } while (input.isEmpty());
        return input;
    }

    /**
     * Read an integer within [min, max], re-prompting on bad input.
     */
    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.printf("  Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }
}
