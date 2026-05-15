package smartlibrary;

/**
 * Wei Feng
 * LibraryUser represents a verified borrower identity mapping.
 *
 * This is stored in users.csv to prevent inconsistent pairs such as:
 * - Same ID with different names
 * - Same name with different IDs
 */
class LibraryUser {
    /** Borrower name. */
    private final String userName;

    /** Borrower unique identifier. */
    private final String userId;

    LibraryUser(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

    String getUserName() {
        return userName;
    }

    String getUserId() {
        return userId;
    }
}