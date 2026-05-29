package smartlibrary;

import java.util.List;

/*
 * Isaac
 * LibraryADT Interface
 * This interface handles information hiding for our system.
 * The UI layer (Main.java) only communicates with these abstract methods,
 * keeping the actual BST, Stack, and CSV operations fully encapsulated.
 */
public interface LibraryADT {


    boolean addBook(int isbn, String title, String author);

    // Returns a list of all books sorted by ISBN using in-order tree traversal
    List<Book> getAllBooks();

    // Searches for a book by its ISBN using recursive tree search
    Book searchBook(int isbn);

    // Basic borrow method for backward compatibility
    boolean borrowBook(int isbn);

    // Main borrow method that takes full borrower details and lending period
    boolean borrowBook(int isbn, String userName, String userId, int lendPeriodDays);

    // Returns the most recently borrowed book (LIFO pop from stack)
    Book returnLatestBorrowed();

    // Returns a specific borrowed book by searching for its ISBN in the stack
    Book returnBookByIsbn(int isbn);

    // Prints out the borrowing history stack in LIFO order
    void viewHistory();

    boolean isCatalogueEmpty();

    // Gets the full list of lending transaction logs from the CSV file
    List<LendingRecord> getLendingRecords();
}

