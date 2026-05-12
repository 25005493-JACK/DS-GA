package smartlibrary;

import java.util.List;

public interface LibraryADT {
    boolean addBook(int isbn, String title, String author);
    List<Book> getAllBooks();
    Book searchBook(int isbn);
    boolean borrowBook(int isbn);
    boolean borrowBook(int isbn, String userName, String userId, int lendPeriodDays);
    Book returnLatestBorrowed();
    void viewHistory();
    boolean isCatalogueEmpty();
    List<LendingRecord> getLendingRecords();
}
