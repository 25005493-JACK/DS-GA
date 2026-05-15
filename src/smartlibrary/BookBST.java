package smartlibrary;

import java.util.ArrayList;
import java.util.List;

/**
 * BookBST is a Binary Search Tree keyed by ISBN.
 *
 * Why BST here:
 * - Fast lookup by key using tree decisions (left/right by comparison).
 * - Recursive search demonstrates assignment requirement for recursive traversal.
 */
class BookBST {
    /** Root node of the BST. Null means empty tree. */
    private BookNode root;

    /**
     * Insert a new book by ISBN ordering.
     * Duplicate ISBN is rejected.
     */
    boolean insert(Book book) {
        if (root == null) {
            root = new BookNode(book);
            return true;
        }
        return insertRecursive(root, book);
    }

    /**
     * Recursive insert:
     * - smaller key -> left subtree
     * - larger key -> right subtree
     * - equal key -> duplicate (reject)
     */
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

    /**
     * Public search entry. Uses recursive traversal.
     */
    Book search(int isbn) {
        return searchRecursive(root, isbn);
    }

    /**
     * Recursive search by ISBN:
     * - base case: null node means not found
     * - hit case: equal key found
     * - move left/right according to key comparison
     */
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

    /**
     * Remove a book by ISBN and return removed payload.
     */
    Book remove(int isbn) {
        Book target = search(isbn);
        if (target == null) {
            return null;
        }
        root = removeRecursive(root, isbn);
        return target;
    }

    /**
     * Recursive delete with three standard BST cases:
     * 1) leaf or single child -> replace node directly
     * 2) two children -> replace with in-order successor (min in right subtree)
     */
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

    /**
     * Find smallest key in a subtree by repeatedly moving left.
     */
    private BookNode findMin(BookNode node) {
        BookNode current = node;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    /**
     * Export all books sorted by ISBN via in-order traversal.
     */
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

    /**
     * @return true if tree has no nodes.
     */
    boolean isEmpty() {
        return root == null;
    }
}