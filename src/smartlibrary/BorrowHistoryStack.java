package smartlibrary;

import java.util.Stack;

class BorrowHistoryStack {
    private final Stack<Book> history = new Stack<>();

    void push(Book book) {
        history.push(book);
    }

    Book pop() {
        if (history.isEmpty()) {
            return null;
        }
        return history.pop();
    }
    boolean isEmpty() {
        return history.isEmpty();
    }

    void showAll() {
        if (history.isEmpty()) {
            System.out.println("Borrow history is empty.");
            return;
        }
        System.out.println("Borrowing History (Most recent first):");
        for (int i = history.size() - 1; i >= 0; i--) {
            Book book = history.get(i);
            System.out.println((history.size() - i) + ". " + book);
        }
    }
}