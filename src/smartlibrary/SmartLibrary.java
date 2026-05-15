package smartlibrary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

/**
 * Isaac
 * SmartLibrary is the concrete implementation of LibraryADT.
 *
 * It orchestrates three responsibilities:
 * 1) Catalogue operations via BookBST
 * 2) LIFO borrowing history via BorrowHistoryStack
 * 3) Persistence via CsvRepository
 */
public class SmartLibrary implements LibraryADT {
    /** BST storing currently available books in catalogue. */
    private final BookBST catalogue = new BookBST();

    /** Stack storing active borrowed books in most-recent-first logic. */
    private final BorrowHistoryStack borrowHistory = new BorrowHistoryStack();

    /** Persistence gateway for CSV files. */
    private final CsvRepository csvRepository = new CsvRepository();

    /** Full lending transaction log loaded from/saved to CSV. */
    private final List<LendingRecord> lendingRecords = new ArrayList<>();

    /** User identity pairs used for borrower validation rules. */
    private final List<LibraryUser> users = new ArrayList<>();

    /**
     * Construct library and immediately hydrate state from CSV files.
     */
    public SmartLibrary() {
        loadFromCsv();
    }

    /**
     * Add book to BST catalogue, then persist catalogue if successful.
     */
    @Override
    public boolean addBook(int isbn, String title, String author) {
        boolean added = catalogue.insert(new Book(isbn, title, author));
        if (added) {
            persistBooks();
        }
        return added;
    }

    /**
     * Recursive BST search wrapper.
     */
    @Override
    public Book searchBook(int isbn) {
        return catalogue.search(isbn);
    }

    /**
     * Return immutable sorted list of all catalogue books.
     */
    @Override
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(catalogue.toInOrderList());
    }

    /**
     * Backward-compatible borrow API with default user values.
     */
    @Override
    public boolean borrowBook(int isbn) {
        return borrowBook(isbn, "Unknown", "Unknown", 14);
    }

    /**
     * Borrow workflow:
     * 1) Validate lending period and user mapping
     * 2) Remove book from catalogue (book becomes unavailable)
     * 3) Push book into borrow history stack
     * 4) Append lending record
     * 5) Persist books and lending records
     */
    @Override
    public boolean borrowBook(int isbn, String userName, String userId, int lendPeriodDays) {
        if (lendPeriodDays <= 0) {
            return false;
        }

        if (!registerOrValidateUser(userName, userId)) {
            return false;
        }

        Book borrowed = catalogue.remove(isbn);
        if (borrowed == null) {
            return false;
        }

        borrowHistory.push(borrowed);

        LocalDateTime lendTime = LocalDateTime.now();
        LocalDateTime dueTime = lendTime.plusDays(lendPeriodDays);
        LendingRecord record = new LendingRecord(
                userName,
                userId,
                borrowed.getTitle(),
                borrowed.getAuthor(),
                borrowed.getIsbn(),
                lendTime,
                lendPeriodDays,
                dueTime,
                null
        );

        lendingRecords.add(record);
        persistBooks();
        persistLendings();
        return true;
    }

    /**
     * Return latest borrowed book (stack pop):
     * - Put book back into catalogue
     * - Mark latest active lending of same ISBN as returned
     * - Persist updated state
     */
    @Override
    public Book returnLatestBorrowed() {
        Book returned = borrowHistory.pop();
        if (returned != null) {
            catalogue.insert(returned);
            markLatestActiveLendingReturned(returned.getIsbn());
            persistBooks();
            persistLendings();
        }
        return returned;
    }

    /**
     * Print current borrow stack.
     */
    @Override
    public void viewHistory() {
        borrowHistory.showAll();
    }

    /**
     * @return true when catalogue has no books.
     */
    @Override
    public boolean isCatalogueEmpty() {
        return catalogue.isEmpty();
    }

    /**
     * Expose lending records as read-only list.
     */
    @Override
    public List<LendingRecord> getLendingRecords() {
        return Collections.unmodifiableList(lendingRecords);
    }

    /**
     * Load books/lendings/users from CSV and rebuild runtime stack state.
     */
    private void loadFromCsv() {
        List<Book> books = csvRepository.loadBooks();
        for (Book book : books) {
            catalogue.insert(book);
        }

        lendingRecords.clear();
        lendingRecords.addAll(csvRepository.loadLendingRecords());

        users.clear();
        users.addAll(csvRepository.loadUsers());

        syncUsersFromLendingRecords();
        rebuildBorrowHistoryFromRecords();
    }

    /** Persist current catalogue snapshot. */
    private void persistBooks() {
        csvRepository.saveBooks(catalogue.toInOrderList());
    }

    /** Persist lending transaction log. */
    private void persistLendings() {
        csvRepository.saveLendingRecords(lendingRecords);
    }

    /** Persist user identity mapping table. */
    private void persistUsers() {
        csvRepository.saveUsers(users);
    }

    /**
     * Mark the latest active record for given ISBN as returned now.
     *
     * Searching from end ensures the most recent matching borrow is updated first.
     */
    private void markLatestActiveLendingReturned(int isbn) {
        for (int i = lendingRecords.size() - 1; i >= 0; i--) {
            LendingRecord record = lendingRecords.get(i);
            if (record.getIsbn() == isbn && record.getReturnTime() == null) {
                record.markReturned(LocalDateTime.now());
                return;
            }
        }
    }

    /**
     * Rebuild runtime borrow stack from active lending records.
     *
     * Active = returnTime is null.
     * Sort ascending by lendTime, then push in that order so stack top becomes
     * the latest borrow, preserving LIFO semantics.
     */
    private void rebuildBorrowHistoryFromRecords() {
        borrowHistory.clear();

        List<LendingRecord> active = new ArrayList<>();
        for (LendingRecord record : lendingRecords) {
            if (record.getReturnTime() == null) {
                active.add(record);
            }
        }

        active.sort(Comparator.comparing(LendingRecord::getLendTime));
        for (LendingRecord record : active) {
            borrowHistory.push(new Book(record.getIsbn(), record.getBookTitle(), record.getAuthor()));
        }
    }

    /**
     * Register a new user pair or validate existing mapping.
     *
     * Rules:
     * - Same ID cannot map to different name
     * - Same name cannot map to different ID
     * - Existing exact pair is valid
     * - New valid pair is added and persisted
     */
    private boolean registerOrValidateUser(String userName, String userId) {
        String normalizedName = userName.trim();
        String normalizedId = userId.trim();

        if (normalizedName.isEmpty() || normalizedId.isEmpty()) {
            return false;
        }

        for (LibraryUser user : users) {
            if (user.getUserId().equalsIgnoreCase(normalizedId)
                    && !user.getUserName().equalsIgnoreCase(normalizedName)) {
                return false;
            }

            if (user.getUserName().equalsIgnoreCase(normalizedName)
                    && !user.getUserId().equalsIgnoreCase(normalizedId)) {
                return false;
            }

            if (user.getUserId().equalsIgnoreCase(normalizedId)
                    && user.getUserName().equalsIgnoreCase(normalizedName)) {
                return true;
            }
        }

        users.add(new LibraryUser(normalizedName, normalizedId));
        persistUsers();
        return true;
    }

    /**
     * Bootstrap helper: ensure all lending-record users exist in users.csv.
     */
    private void syncUsersFromLendingRecords() {
        boolean changed = false;

        for (LendingRecord record : lendingRecords) {
            String name = record.getUserName().trim();
            String id = record.getUserId().trim();
            if (name.isEmpty() || id.isEmpty()) {
                continue;
            }

            boolean exists = false;
            for (LibraryUser user : users) {
                if (user.getUserId().equalsIgnoreCase(id) && user.getUserName().equalsIgnoreCase(name)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                users.add(new LibraryUser(name, id));
                changed = true;
            }
        }

        if (changed) {
            persistUsers();
        }
    }
}