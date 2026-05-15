package smartlibrary;

import java.util.List;

/**
 * LibraryADT defines the public contract of the library system.
 *
 * This interface is used to enforce information hiding:
 * - UI layer (Main) talks only to this API.
 * - Internal implementations (BST, Stack, CSV) stay encapsulated.
 */
public interface LibraryADT {

    /**
     * Add a new book into the catalogue.
     *
     * @param isbn unique book ISBN used as BST key
     * @param title book title
     * @param author book author
     * @return true if inserted, false if ISBN already exists
     */
    boolean addBook(int isbn, String title, String author);

    /**
     * Get all books sorted by ISBN (in-order BST traversal).
     *
     * @return immutable view of all books
     */
    List<Book> getAllBooks();

    /**
     * Search a book by ISBN using BST recursive search.
     *
     * @param isbn target ISBN
     * @return matched Book or null if not found
     */
    Book searchBook(int isbn);

    /**
     * Borrow a book with default borrower metadata.
     * Kept for backward compatibility.
     *
     * @param isbn target ISBN
     * @return true if borrow success
     */
    boolean borrowBook(int isbn);

    /**
     * Borrow a book with complete borrower details.
     *
     * @param isbn target ISBN
     * @param userName borrower name
     * @param userId borrower id
     * @param lendPeriodDays borrowing period (days)
     * @return true if borrow success
     */
    boolean borrowBook(int isbn, String userName, String userId, int lendPeriodDays);

    /**
     * Return the latest borrowed book (LIFO from stack).
     *
     * @return returned Book or null when no active borrowed item
     */
    Book returnLatestBorrowed();

    /**
     * Print borrowing history in LIFO order.
     */
    void viewHistory();

    /**
     * Quick check if catalogue is empty.
     *
     * @return true when catalogue has no books
     */
    boolean isCatalogueEmpty();

    /**
     * Get all lending transaction records.
     *
     * @return immutable view of lending records
     */
    List<LendingRecord> getLendingRecords();
}