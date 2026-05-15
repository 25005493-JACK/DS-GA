package smartlibrary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Wei Feng + Xin Yi
 * Main is the console UI entry point.
 *
 * It handles:
 * - Menu loop
 * - Input validation
 * - Calling domain operations through LibraryADT
 */
public class Main {
    /** Formatter used in table display for lending timestamps. */
    private static final DateTimeFormatter TABLE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** Library business logic exposed via interface. */
    private final LibraryADT library;

    /** Shared scanner for user input. */
    private final Scanner scanner;

    public Main() {
        this.library = new SmartLibrary();
        this.scanner = new Scanner(System.in);
    }

    /** Program entry point. */
    public static void main(String[] args) {
        new Main().run();
    }

    /**
     * Main UI loop. Keeps showing menu until user exits.
     */
    private void run() {
        System.out.println("Smart Library System (Java + BST + Stack + CSV)");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> handleAddBook();
                case 2 -> handleViewAllBooks();
                case 3 -> handleSearchBook();
                case 4 -> handleBorrowBook();
                case 5 -> handleReturnLatestBorrowed();
                case 6 -> handleViewLendingRecords();
                case 7 -> library.viewHistory();
                case 8 -> {
                    System.out.println("Exiting program.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please choose 1-8.");
            }
        }
    }

    /** Print menu options in workflow-friendly order. */
    private void printMenu() {
        System.out.println();
        System.out.println("==== Smart Library Menu ====");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Book");
        System.out.println("4. Borrow Book");
        System.out.println("5. Return Latest Borrowed Book");
        System.out.println("6. View Lending Records");
        System.out.println("7. View Borrowing History");
        System.out.println("8. Exit");
    }

    /** Gather input and add a book to catalogue. */
    private void handleAddBook() {
        int isbn = readInt("Enter ISBN (integer): ");
        String title = readNonEmptyLine("Enter title: ");
        String author = readNonEmptyLine("Enter author: ");

        boolean added = library.addBook(isbn, title, author);
        if (added) {
            System.out.println("Book added successfully.");
        } else {
            System.out.println("ISBN already exists. Book was not added.");
        }
    }

    /** Search and print one book by ISBN. */
    private void handleSearchBook() {
        int isbn = readInt("Enter ISBN to search: ");
        Book book = library.searchBook(isbn);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Book found: " + book);
    }

    /**
     * Print all catalogue books in aligned table format.
     */
    private void handleViewAllBooks() {
        List<Book> books = library.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in catalogue.");
            return;
        }

        System.out.println("All Books in Catalogue (ISBN order):");
        System.out.println("+----+----------+--------------------------------------+------------------------+");
        System.out.printf("| %-2s | %-8s | %-36s | %-22s |%n", "No", "ISBN", "Title", "Author");
        System.out.println("+----+----------+--------------------------------------+------------------------+");

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.printf(
                    "| %2d | %8d | %-36.36s | %-22.22s |%n",
                    i + 1,
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor()
            );
        }

        System.out.println("+----+----------+--------------------------------------+------------------------+");
    }

    /**
     * Borrow a book with validated borrower details and lending period.
     */
    private void handleBorrowBook() {
        int isbn = readInt("Enter ISBN to borrow: ");
        String userName = readNonEmptyLine("Enter borrower name: ");
        String userId = readNonEmptyLine("Enter borrower ID: ");
        int lendPeriodDays = readPositiveInt("Enter lending period in days: ");

        boolean borrowed = library.borrowBook(isbn, userName, userId, lendPeriodDays);

        if (borrowed) {
            System.out.println("Book borrowed and moved to history stack.");
        } else {
            System.out.println("Borrow failed. Book not found, invalid lending period, or user/name ID mismatch.");
        }
    }

    /**
     * Print lending records in aligned table format.
     */
    private void handleViewLendingRecords() {
        List<LendingRecord> records = library.getLendingRecords();
        if (records.isEmpty()) {
            System.out.println("No lending records found.");
            return;
        }

        System.out.println("Lending Records:");
        LocalDateTime now = LocalDateTime.now();

        System.out.println("+----+----------------+----------+----------------------------+----------+------------------+------------------+--------+----------+");
        System.out.printf(
                "| %-2s | %-14s | %-8s | %-26s | %-8s | %-16s | %-16s | %-6s | %-8s |%n",
                "No", "User", "User ID", "Book Title", "ISBN", "Lend Time", "Due Time", "Exceed", "Status"
        );
        System.out.println("+----+----------------+----------+----------------------------+----------+------------------+------------------+--------+----------+");

        for (int i = 0; i < records.size(); i++) {
            LendingRecord record = records.get(i);
            long exceededDays = record.getExceededDays(now);
            String status = (record.getReturnTime() == null) ? "ACTIVE" : "RETURNED";

            System.out.printf(
                    "| %2d | %-14.14s | %-8.8s | %-26.26s | %8d | %-16s | %-16s | %6d | %-8.8s |%n",
                    i + 1,
                    record.getUserName(),
                    record.getUserId(),
                    record.getBookTitle(),
                    record.getIsbn(),
                    record.getLendTime().format(TABLE_TIME_FORMAT),
                    record.getDueTime().format(TABLE_TIME_FORMAT),
                    exceededDays,
                    status
            );
        }

        System.out.println("+----+----------------+----------+----------------------------+----------+------------------+------------------+--------+----------+");
    }

    /** Return most recent borrowed book and display borrower details. */
    private void handleReturnLatestBorrowed() {
        Book returned = library.returnLatestBorrowed();
        if (returned == null) {
            System.out.println("No borrowed books in history to return.");
        } else {
            LendingRecord returnedRecord = findLatestReturnedRecord(returned.getIsbn());
            String borrowerName = (returnedRecord == null) ? "Unknown" : returnedRecord.getUserName();
            String borrowerId = (returnedRecord == null) ? "Unknown" : returnedRecord.getUserId();

            System.out.println("Book Returned Successfully!");
            System.out.println("+----------+--------------------------------------+------------------------+----------------+----------+");
            System.out.printf("| %-8s | %-36s | %-22s | %-14s | %-8s |%n", "ISBN", "Title", "Author", "Borrower", "User ID");
            System.out.println("+----------+--------------------------------------+------------------------+----------------+----------+");
            System.out.printf("| %8d | %-36.36s | %-22.22s | %-14.14s | %-8.8s |%n",
                    returned.getIsbn(),
                    returned.getTitle(),
                    returned.getAuthor(),
                    borrowerName,
                    borrowerId);
            System.out.println("+----------+--------------------------------------+------------------------+----------------+----------+");
        }
    }

    /** Find the most recent returned lending record for a given ISBN. */
    private LendingRecord findLatestReturnedRecord(int isbn) {
        List<LendingRecord> records = library.getLendingRecords();
        for (int i = records.size() - 1; i >= 0; i--) {
            LendingRecord record = records.get(i);
            if (record.getIsbn() == isbn && record.getReturnTime() != null) {
                return record;
            }
        }
        return null;
    }

    /**
     * Read an integer with retry loop on invalid inputs.
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    /**
     * Read integer that must be > 0.
     */
    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than 0.");
        }
    }

    /**
     * Read non-empty string input.
     */
    private String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
}
