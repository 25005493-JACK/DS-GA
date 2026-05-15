package smartlibrary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LendingRecord represents one lending transaction row.
 *
 * This class maps directly to one CSV row in lendings.csv.
 */
public class LendingRecord {
    /** Date-time formatter used for CSV serialization/deserialization. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /** Borrower display name. */
    private final String userName;

    /** Borrower unique ID. */
    private final String userId;

    /** Borrowed book title (snapshot at lend time). */
    private final String bookTitle;

    /** Borrowed book author. */
    private final String author;
    
    /** Borrowed book ISBN. */
    private final int isbn;

    /** Lending timestamp. */
    private final LocalDateTime lendTime;

    /** Allowed lending period in days. */
    private final int lendPeriodDays;

    /** Computed due timestamp. */
    private final LocalDateTime dueTime;

    /** Return timestamp; null means still active/unreturned. */
    private LocalDateTime returnTime;

    public LendingRecord(
            String userName,
            String userId,
            String bookTitle,
            String author,
            int isbn,
            LocalDateTime lendTime,
            int lendPeriodDays,
            LocalDateTime dueTime,
            LocalDateTime returnTime
    ) {
        this.userName = userName;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.lendTime = lendTime;
        this.lendPeriodDays = lendPeriodDays;
        this.dueTime = dueTime;
        this.returnTime = returnTime;
    }

    public String getUserName() { return userName; }
    public String getUserId() { return userId; }
    public String getBookTitle() { return bookTitle; }
    public String getAuthor() { return author; }
    public int getIsbn() { return isbn; }
    public LocalDateTime getLendTime() { return lendTime; }
    public int getLendPeriodDays() { return lendPeriodDays; }
    public LocalDateTime getDueTime() { return dueTime; }
    public LocalDateTime getReturnTime() { return returnTime; }

    /**
     * Mark this record as returned at specific time.
     */
    public void markReturned(LocalDateTime time) {
        this.returnTime = time;
    }

    /**
     * Calculate exceeded days against due date.
     *
     * If already returned, compare dueTime vs returnTime.
     * If still active, compare dueTime vs provided reference time (usually now).
     */
    public long getExceededDays(LocalDateTime referenceTime) {
        LocalDateTime compareTime = (returnTime != null) ? returnTime : referenceTime;
        if (compareTime.isAfter(dueTime)) {
            long hours = java.time.Duration.between(dueTime, compareTime).toHours();
            return Math.max(1, (long) Math.ceil(hours / 24.0));
        }
        return 0;
    }

    /**
     * Convert object to CSV row columns (9-column schema).
     */
    String[] toCsvRow() {
        return new String[] {
                userName,
                userId,
                bookTitle,
                author,
                String.valueOf(isbn),
                lendTime.format(DATE_TIME_FORMATTER),
                String.valueOf(lendPeriodDays),
                dueTime.format(DATE_TIME_FORMATTER),
                returnTime == null ? "" : returnTime.format(DATE_TIME_FORMATTER)
        };
    }

    /**
     * Parse one CSV row into LendingRecord.
     * Expected schema:
     * - 9-column: userName,userId,bookTitle,author,isbn,lendTime,lendPeriodDays,dueTime,returnTime
     * - 8-column (legacy): userName,userId,bookTitle,isbn,lendTime,lendPeriodDays,dueTime,returnTime
     */
    static LendingRecord fromCsvRow(String[] cols) {
        if (cols.length != 8 && cols.length != 9) {
            throw new IllegalArgumentException("Expected 8 or 9 columns, got " + cols.length);
        }

        String userName = cols[0];
        String userId = cols[1];
        String bookTitle = cols[2];
        String author;
        int isbn;
        LocalDateTime lendTime;
        int lendPeriodDays;
        LocalDateTime dueTime;
        LocalDateTime returnTime;

        if (cols.length == 9) {
            author = cols[3];
            isbn = Integer.parseInt(cols[4]);
            lendTime = LocalDateTime.parse(cols[5], DATE_TIME_FORMATTER);
            lendPeriodDays = Integer.parseInt(cols[6]);
            dueTime = LocalDateTime.parse(cols[7], DATE_TIME_FORMATTER);
            returnTime = cols[8].isBlank() ? null : LocalDateTime.parse(cols[8], DATE_TIME_FORMATTER);
        } else {
            // Backward compatibility for older files that do not store author.
            author = "Unknown";
            isbn = Integer.parseInt(cols[3]);
            lendTime = LocalDateTime.parse(cols[4], DATE_TIME_FORMATTER);
            lendPeriodDays = Integer.parseInt(cols[5]);
            dueTime = LocalDateTime.parse(cols[6], DATE_TIME_FORMATTER);
            returnTime = cols[7].isBlank() ? null : LocalDateTime.parse(cols[7], DATE_TIME_FORMATTER);
        }

        return new LendingRecord(userName, userId, bookTitle, author, isbn, lendTime, lendPeriodDays, dueTime, returnTime);
    }
}
