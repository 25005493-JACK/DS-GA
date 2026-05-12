package smartlibrary;

public interface LibraryADT {
    boolean addBook(int isbn, String title, String author);
    Book searchBook(int isbn);
    boolean borrowBook(int isbn);
    Book returnLatestBorrowed();
    void viewHistory();
    boolean isCatalogueEmpty();
}