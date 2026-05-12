package smartlibrary;

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
        System.out.println("Smart Library System (Java + BST + Stack)");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> handleAddBook();
                case 2 -> handleSearchBook();
                case 3 -> handleBorrowBook();
                case 4 -> library.viewHistory();
                case 5 -> {
                    System.out.println("Exiting program.");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==== Smart Library Menu ====");
        System.out.println("1. Add Book");
        System.out.println("2. Search Book");
        System.out.println("3. Borrow Book");
        System.out.println("4. View History");
        System.out.println("5. Exit");
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

    private void handleBorrowBook() {
        int isbn = readInt("Enter ISBN to borrow: ");
        boolean borrowed = library.borrowBook(isbn);

        if (borrowed) {
            System.out.println("Book borrowed and moved to history stack.");
        } else {
            System.out.println("Book not found in catalogue.");
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