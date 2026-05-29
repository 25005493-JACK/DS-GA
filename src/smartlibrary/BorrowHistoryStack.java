package smartlibrary;

import java.util.Stack;

/*
 * Xin Yi
 * BorrowHistoryStack manages actively borrowed books using a Stack.
 * This satisfies the LIFO (Last-In, First-Out) assignment requirement,
 * meaning the most recently borrowed book will be at the top of our list.
 */

class BorrowHistoryStack {
    // Internal stack container to hold our book objects
    private final Stack<Book> history = new Stack<>();

    // Pushes a borrowed book onto the top of the stack
    void push(Book book) {
        history.push(book);
    }

    // Pops and returns the most recently borrowed book from the top
    Book pop() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }

    /*
     * Loops through the stack backward to find and remove a specific book by its ISBN.
     * This is used when a student returns a specific book instead of the latest one.
     */
    Book removeByIsbn(int isbn) {
        for (int i = history.size() - 1; i >= 0; i--) {
            Book book = history.get(i);
            if (book.getIsbn() == isbn) {
                return history.remove(i);
            }
        }
        return null;
    }

    // Clears the entire stack (helpful when rebuilding data from the CSV file at startup)
    void clear() {
        history.clear();
    }

    // Checks if there are any active loans left in the stack
    boolean isEmpty() {
        return history.isEmpty();
    }

    // Prints out the full active borrowing history, showing the newest checkouts first
    void showAll() {
        if (history.isEmpty()) {
            System.out.println("Borrow history is empty.");
            return;
        }

        System.out.println("Borrowing History (Most recent first)");
        System.out.println("+----+----------+--------------------------------------+------------------------+");
        System.out.printf("| %-2s | %-8s | %-36s | %-22s |%n", "No", "ISBN", "Title", "Author");
        System.out.println("+----+----------+--------------------------------------+------------------------+");

        // Loop backward from the top of the stack down to index 0
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