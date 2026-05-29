package smartlibrary;

/**
 * Ying Chen
 * Internal BST node that stores one Book and references to child nodes.
 *
 * This class is package-private and only used by BookBST.
 */
class BookNode {
    /** Payload book for this node. */
    private final Book book;

    /** Left subtree: all ISBN smaller than current node ISBN. */
    private BookNode left;

    /** Right subtree: all ISBN greater than current node ISBN. */
    private BookNode right;

    BookNode(Book book) {
        this.book = book;
    }

    Book getBook() {
        return book;
    }

    BookNode getLeft() {
        return left;
    }

    void setLeft(BookNode left) {
        this.left = left;
    }

    BookNode getRight() {
        return right;
    }

    void setRight(BookNode right) {
        this.right = right;
    }
}
