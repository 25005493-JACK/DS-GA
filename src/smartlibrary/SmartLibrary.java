package smartlibrary;

public class SmartLibrary implements LibraryADT {
    private final BookBST catalogue = new BookBST();
    private final BorrowHistoryStack borrowHistory = new BorrowHistoryStack();

    @Override
    public boolean addBook(int isbn, String title, String author) {
        return catalogue.insert(new Book(isbn, title, author));
    }

    @Override
    public Book searchBook(int isbn) {
        return catalogue.search(isbn);
    }

    @Override
    public boolean borrowBook(int isbn) {
        Book borrowed = catalogue.remove(isbn);
        if (borrowed == null) {
            return false;
        }
        borrowHistory.push(borrowed);
        return true;
    }

    @Override
    public Book returnLatestBorrowed() {
        Book returned = borrowHistory.pop();
        if (returned != null) {
            catalogue.insert(returned);
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
}