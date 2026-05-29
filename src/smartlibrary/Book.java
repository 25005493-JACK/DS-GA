package smartlibrary;

/*
 * Ying Chen
 * Book Entity Class
 * This class represents a single book object. 
 * We made it immutable (using final variables) so that the book details 
 * won't accidentally get changed when moving through the BST or Stack.
 */

public class Book {
    // Unique key for BST sorting and search
    private final int isbn;
    private final String title;
    private final String author;

    // Constructor to initialize book details
    public Book(int isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    // Getters
    public int getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    // To display book info nicely in tables/logs
    @Override
    public String toString() {
        return "ISBN: " + isbn + " | Title: " + title + " | Author: " + author;
    }
}