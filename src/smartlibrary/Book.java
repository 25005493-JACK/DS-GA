package smartlibrary;

/**
 * Ying Chen
 * Immutable domain model representing a single book entity.
 *
 * Book is intentionally immutable so once created, its key properties
 * (ISBN/title/author) do not change unexpectedly while it is stored in BST
 * or moved through borrowing history.
 */
public class Book {
    /** Unique key used by BST ordering and lookup. */
    private final int isbn;

    /** Display title of the book. */
    private final String title;

    /** Author name of the book. */
    private final String author;

    /**
     * Create a book instance.
     */
    public Book(int isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    /** @return ISBN key */
    public int getIsbn() {
        return isbn;
    }

    /** @return title text */
    public String getTitle() {
        return title;
    }

    /** @return author text */
    public String getAuthor() {
        return author;
    }

    /**
     * Readable summary for quick logging/debug output.
     */
    @Override
    public String toString() {
        return "ISBN: " + isbn + " | Title: " + title + " | Author: " + author;
    }
}