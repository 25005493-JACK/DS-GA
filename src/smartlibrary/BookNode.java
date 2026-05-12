package smartlibrary;

class BookNode {
    private final Book book;
    private BookNode left;
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