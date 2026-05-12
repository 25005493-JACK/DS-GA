package smartlibrary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

public class SmartLibrary implements LibraryADT {
    private final BookBST catalogue = new BookBST();
    private final BorrowHistoryStack borrowHistory = new BorrowHistoryStack();
    private final CsvRepository csvRepository = new CsvRepository();
    private final List<LendingRecord> lendingRecords = new ArrayList<>();

    public SmartLibrary() {
        loadFromCsv();
    }

    @Override
    public boolean addBook(int isbn, String title, String author) {
        boolean added = catalogue.insert(new Book(isbn, title, author));
        if (added) {
            persistBooks();
        }
        return added;
    }

    @Override
    public Book searchBook(int isbn) {
        return catalogue.search(isbn);
    }

    @Override
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(catalogue.toInOrderList());
    }

    @Override
    public boolean borrowBook(int isbn) {
        return borrowBook(isbn, "Unknown", "Unknown", 14);
    }

    @Override
    public boolean borrowBook(int isbn, String userName, String userId, int lendPeriodDays) {
        if (lendPeriodDays <= 0) {
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

    @Override
    public void viewHistory() {
        borrowHistory.showAll();
    }

    @Override
    public boolean isCatalogueEmpty() {
        return catalogue.isEmpty();
    }

    @Override
    public List<LendingRecord> getLendingRecords() {
        return Collections.unmodifiableList(lendingRecords);
    }

    private void loadFromCsv() {
        List<Book> books = csvRepository.loadBooks();
        for (Book book : books) {
            catalogue.insert(book);
        }

        lendingRecords.clear();
        lendingRecords.addAll(csvRepository.loadLendingRecords());
        rebuildBorrowHistoryFromRecords();
    }

    private void persistBooks() {
        csvRepository.saveBooks(catalogue.toInOrderList());
    }

    private void persistLendings() {
        csvRepository.saveLendingRecords(lendingRecords);
    }

    private void markLatestActiveLendingReturned(int isbn) {
        for (int i = lendingRecords.size() - 1; i >= 0; i--) {
            LendingRecord record = lendingRecords.get(i);
            if (record.getIsbn() == isbn && record.getReturnTime() == null) {
                record.markReturned(LocalDateTime.now());
                return;
            }
        }
    }

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
            borrowHistory.push(new Book(record.getIsbn(), record.getBookTitle(), record.getAuthorName()));
        }
    }
}
