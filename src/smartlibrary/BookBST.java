package smartlibrary;

import java.util.ArrayList;
import java.util.List;

class BookBST {
    private BookNode root;

    boolean insert(Book book) {
        if (root == null) {
            root = new BookNode(book);
            return true;
        }
        return insertRecursive(root, book);
    }

    private boolean insertRecursive(BookNode current, Book book) {
        int currentIsbn = current.getBook().getIsbn();
        if (book.getIsbn() == currentIsbn) {
            return false;
        }
        if (book.getIsbn() < currentIsbn) {
            if (current.getLeft() == null) {
                current.setLeft(new BookNode(book));
                return true;
            }
            return insertRecursive(current.getLeft(), book);
        }
        if (current.getRight() == null) {
            current.setRight(new BookNode(book));
            return true;
        }
        return insertRecursive(current.getRight(), book);
    }

    Book search(int isbn) {
        return searchRecursive(root, isbn);
    }

    private Book searchRecursive(BookNode node, int isbn) {
        if (node == null) {
            return null;
        }
        int nodeIsbn = node.getBook().getIsbn();
        if (isbn == nodeIsbn) {
            return node.getBook();
        }
        if (isbn < nodeIsbn) {
            return searchRecursive(node.getLeft(), isbn);
        }
        return searchRecursive(node.getRight(), isbn);
    }

    Book remove(int isbn) {
        Book target = search(isbn);
        if (target == null) {
            return null;
        }
        root = removeRecursive(root, isbn);
        return target;
    }

    private BookNode removeRecursive(BookNode node, int isbn) {
        if (node == null) {
            return null;
        }
        int nodeIsbn = node.getBook().getIsbn();
        if (isbn < nodeIsbn) {
            node.setLeft(removeRecursive(node.getLeft(), isbn));
            return node;
        }
        if (isbn > nodeIsbn) {
            node.setRight(removeRecursive(node.getRight(), isbn));
            return node;
        }
        if (node.getLeft() == null) {
            return node.getRight();
        }
        if (node.getRight() == null) {
            return node.getLeft();
        }
        BookNode successor = findMin(node.getRight());
        BookNode merged = new BookNode(successor.getBook());
        merged.setLeft(node.getLeft());
        merged.setRight(removeRecursive(node.getRight(), successor.getBook().getIsbn()));
        return merged;
    }

    private BookNode findMin(BookNode node) {
        BookNode current = node;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    List<Book> toInOrderList() {
        List<Book> books = new ArrayList<>();
        toInOrderListRecursive(root, books);
        return books;
    }

    private void toInOrderListRecursive(BookNode node, List<Book> books) {
        if (node == null) {
            return;
        }
        toInOrderListRecursive(node.getLeft(), books);
        books.add(node.getBook());
        toInOrderListRecursive(node.getRight(), books);
    }

    boolean isEmpty() {
        return root == null;
    }
}