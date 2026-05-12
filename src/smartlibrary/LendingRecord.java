package smartlibrary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LendingRecord {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String userName;
    private final String userId;
    private final String bookTitle;
    private final int isbn;
    private final LocalDateTime lendTime;
    private final int lendPeriodDays;
    private final LocalDateTime dueTime;
    private LocalDateTime returnTime;

    public LendingRecord(
            String userName,
            String userId,
            String bookTitle,
            int isbn,
            LocalDateTime lendTime,
            int lendPeriodDays,
            LocalDateTime dueTime,
            LocalDateTime returnTime
    ) {
        this.userName = userName;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.lendTime = lendTime;
        this.lendPeriodDays = lendPeriodDays;
        this.dueTime = dueTime;
        this.returnTime = returnTime;
    }

    public String getUserName() { return userName; }
    public String getUserId() { return userId; }
    public String getBookTitle() { return bookTitle; }
    public int getIsbn() { return isbn; }
    public LocalDateTime getLendTime() { return lendTime; }
    public int getLendPeriodDays() { return lendPeriodDays; }
    public LocalDateTime getDueTime() { return dueTime; }
    public LocalDateTime getReturnTime() { return returnTime; }

    public void markReturned(LocalDateTime time) {
        this.returnTime = time;
    }

    public long getExceededDays(LocalDateTime referenceTime) {
        LocalDateTime compareTime = returnTime != null ? returnTime : referenceTime;
        if (compareTime.isAfter(dueTime)) {
            long hours = java.time.Duration.between(dueTime, compareTime).toHours();
            return Math.max(1, (long) Math.ceil(hours / 24.0));
        }
        return 0;
    }

    String[] toCsvRow() {
        return new String[] {
                userName,
                userId,
                bookTitle,
                String.valueOf(isbn),
                lendTime.format(DATE_TIME_FORMATTER),
                String.valueOf(lendPeriodDays),
                dueTime.format(DATE_TIME_FORMATTER),
                returnTime == null ? "" : returnTime.format(DATE_TIME_FORMATTER)
        };
    }

    static LendingRecord fromCsvRow(String[] cols) {
        String userName = cols[0];
        String userId = cols[1];
        String bookTitle = cols[2];
        int isbn = Integer.parseInt(cols[3]);
        LocalDateTime lendTime = LocalDateTime.parse(cols[4], DATE_TIME_FORMATTER);
        int lendPeriodDays = Integer.parseInt(cols[5]);
        LocalDateTime dueTime = LocalDateTime.parse(cols[6], DATE_TIME_FORMATTER);
        LocalDateTime returnTime = cols[7].isBlank() ? null : LocalDateTime.parse(cols[7], DATE_TIME_FORMATTER);
        return new LendingRecord(userName, userId, bookTitle, isbn, lendTime, lendPeriodDays, dueTime, returnTime);
    }

    @Override
    public String toString() {
        LocalDateTime now = LocalDateTime.now();
        long exceededDays = getExceededDays(now);
        String status = returnTime == null ? "ACTIVE" : "RETURNED";
        return "User=" + userName
                + " (" + userId + ")"
                + " | Book=" + bookTitle
                + " | ISBN=" + isbn
                + " | Lend=" + lendTime
                + " | PeriodDays=" + lendPeriodDays
                + " | Due=" + dueTime
                + " | Return=" + (returnTime == null ? "-" : returnTime)
                + " | ExceededDays=" + exceededDays
                + " | Status=" + status;
    }
}