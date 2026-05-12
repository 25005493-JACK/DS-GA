package smartlibrary;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final LibraryADT library;
    private final Scanner scanner;

    public Main() {
        this.library = new SmartLibrary();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        new Main().run();
    }

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

    private void handleSearchBook() {
        int isbn = readInt("Enter ISBN to search: ");
        Book book = library.searchBook(isbn);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Book found: " + book);
    }

    private void handleViewAllBooks() {
        List<Book> books = library.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in catalogue.");
            return;
        }

        System.out.println("All Books in Catalogue (ISBN order):");
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    private void handleBorrowBook() {
        int isbn = readInt("Enter ISBN to borrow: ");
        String userName = readNonEmptyLine("Enter borrower name: ");
        String userId = readNonEmptyLine("Enter borrower ID: ");
        int lendPeriodDays = readPositiveInt("Enter lending period in days: ");

        boolean borrowed = library.borrowBook(isbn, userName, userId, lendPeriodDays);

        if (borrowed) {
            System.out.println("Book borrowed and moved to history stack.");
        } else {
            System.out.println("Borrow failed. Book not found or invalid lending period.");
        }
    }

    private void handleViewLendingRecords() {
        List<LendingRecord> records = library.getLendingRecords();
        if (records.isEmpty()) {
            System.out.println("No lending records found.");
            return;
        }

        System.out.println("Lending Records:");
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < records.size(); i++) {
            LendingRecord record = records.get(i);
            long exceededDays = record.getExceededDays(now);
            System.out.println((i + 1) + ". " + record + " | ExceededNow=" + exceededDays);
        }
    }

    private void handleReturnLatestBorrowed() {
        Book returned = library.returnLatestBorrowed();
        if (returned == null) {
            System.out.println("No borrowed books in history to return.");
        } else {
            System.out.println("Returned: " + returned);
        }
    }

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

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than 0.");
        }
    }

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
