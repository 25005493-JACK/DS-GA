package smartlibrary;

import java.util.Stack;

/**
 * Xin Yi
 * BorrowHistoryStack stores currently borrowed books in LIFO order.
 *
 * LIFO means the most recently borrowed book is returned first, matching
 * stack behavior and assignment requirement.
 */
class BorrowHistoryStack {
    /** Internal stack container. */
    private final Stack<Book> history = new Stack<>();

    /** Push a borrowed book to top of history stack. */
    void push(Book book) {
        history.push(book);
    }

    /**
     * Pop most recent borrowed book.
     *
     * @return top book or null if stack empty
     */
    Book pop() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }

    /** Clear all history entries (used during startup rebuild). */
    void clear() {
        history.clear();
    }

    /** @return true if no active borrowed books in stack */
    boolean isEmpty() {
        return history.isEmpty();
    }

    /**
     * Print history in table format, most recent first.
     */
    void showAll() {
        if (history.isEmpty()) {
            System.out.println("Borrow history is empty.");
            return;
        }

        System.out.println("Borrowing History (Most recent first) [TABLE-V2]:");
        System.out.println("+----+----------+--------------------------------------+------------------------+");
        System.out.printf("| %-2s | %-8s | %-36s | %-22s |%n", "No", "ISBN", "Title", "Author");
        System.out.println("+----+----------+--------------------------------------+------------------------+");

        for (int i = history.size() - 1; i >= 0; i--) {
            Book book = history.get(i);
            System.out.printf(
                    "| %2d | %8d | %-36.36s | %-22.22s |%n",
                    (history.size() - i),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor()
            );
        }

        System.out.println("+----+----------+--------------------------------------+------------------------+");
    }
}