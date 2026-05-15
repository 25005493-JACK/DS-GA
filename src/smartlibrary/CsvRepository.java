package smartlibrary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvRepository handles all low-level CSV persistence.
 *
 * Responsibilities:
 * - Ensure files exist with headers
 * - Load typed objects from CSV rows
 * - Save objects back to CSV files
 *
 * Note: This class intentionally uses simple file APIs for readability
 * and beginner-friendly implementation style.
 */
class CsvRepository {
    /** File path for catalogue books. */
    private static final Path BOOKS_FILE = Paths.get("data", "books.csv");

    /** File path for lending transactions. */
    private static final Path LENDINGS_FILE = Paths.get("data", "lendings.csv");

    /** File path for verified user mappings. */
    private static final Path USERS_FILE = Paths.get("data", "users.csv");

    /**
     * Load all books from books.csv.
     */
    List<Book> loadBooks() {
        ensureFileWithHeader(BOOKS_FILE, "isbn,title,author");

        List<Book> books = new ArrayList<>();
        List<String> lines = readAllLinesSafe(BOOKS_FILE);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cols = parseCsvLine(line);
            if (cols.length != 3) {
                throw new IllegalStateException("Invalid books CSV row: " + line);
            }

            books.add(new Book(Integer.parseInt(cols[0]), cols[1], cols[2]));
        }
        return books;
    }

    /**
     * Overwrite books.csv with current in-memory books list.
     */
    void saveBooks(List<Book> books) {
        ensureParentDirectory(BOOKS_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE.toFile(), false))) {
            writer.write("isbn,title,author");
            writer.newLine();

            for (Book book : books) {
                writer.write(toCsvLine(new String[] {
                        String.valueOf(book.getIsbn()),
                        book.getTitle(),
                        book.getAuthor()
                }));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save books CSV", e);
        }
    }

    /**
     * Load lending transaction records from lendings.csv.
     */
    List<LendingRecord> loadLendingRecords() {
        ensureFileWithHeader(LENDINGS_FILE, "userName,userId,bookTitle,author,isbn,lendTime,lendPeriodDays,dueTime,returnTime");

        List<LendingRecord> records = new ArrayList<>();
        List<String> lines = readAllLinesSafe(LENDINGS_FILE);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cols = parseCsvLine(line);
            if (cols.length != 8 && cols.length != 9) {
                throw new IllegalStateException("Invalid lending CSV row at line " + (i + 1) + ": " + line);
            }

            records.add(LendingRecord.fromCsvRow(cols));
        }
        return records;
    }

    /**
     * Overwrite lendings.csv with current lending records.
     */
    void saveLendingRecords(List<LendingRecord> records) {
        ensureParentDirectory(LENDINGS_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LENDINGS_FILE.toFile(), false))) {
            writer.write("userName,userId,bookTitle,author,isbn,lendTime,lendPeriodDays,dueTime,returnTime");
            writer.newLine();

            for (LendingRecord record : records) {
                writer.write(toCsvLine(record.toCsvRow()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save lending CSV", e);
        }
    }

    /**
     * Load users.csv mappings.
     */
    List<LibraryUser> loadUsers() {
        ensureFileWithHeader(USERS_FILE, "userName,userId");

        List<LibraryUser> users = new ArrayList<>();
        List<String> lines = readAllLinesSafe(USERS_FILE);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cols = parseCsvLine(line);
            if (cols.length != 2) {
                throw new IllegalStateException("Invalid users CSV row: " + line);
            }

            users.add(new LibraryUser(cols[0], cols[1]));
        }
        return users;
    }

    /**
     * Overwrite users.csv with current verified user pairs.
     */
    void saveUsers(List<LibraryUser> users) {
        ensureParentDirectory(USERS_FILE);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE.toFile(), false))) {
            writer.write("userName,userId");
            writer.newLine();

            for (LibraryUser user : users) {
                writer.write(toCsvLine(new String[] {user.getUserName(), user.getUserId()}));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save users CSV", e);
        }
    }

    /**
     * Ensure CSV file exists with provided header.
     */
    private void ensureFileWithHeader(Path file, String header) {
        ensureParentDirectory(file);
        if (Files.exists(file)) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), false))) {
            writer.write(header);
            writer.newLine();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create file: " + file, e);
        }
    }

    /**
     * Ensure parent folder exists before writing file.
     */
    private void ensureParentDirectory(Path file) {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory for: " + file, e);
        }
    }

    /**
     * Read all lines and convert checked exception into runtime exception.
     */
    private List<String> readAllLinesSafe(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + file, e);
        }
    }

    /**
     * Serialize string columns into one CSV line with escaping.
     */
    private String toCsvLine(String[] columns) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escape(columns[i]));
        }
        return builder.toString();
    }

    /**
     * Escape CSV field if it contains comma, quote, or newline.
     */
    private String escape(String value) {
        String safe = (value == null) ? "" : value;
        boolean needQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");

        if (!needQuotes) {
            return safe;
        }

        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

    /**
     * Parse one CSV line into columns with quote-awareness.
     */
    private String[] parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                cols.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        cols.add(current.toString());
        return cols.toArray(new String[0]);
    }
}
