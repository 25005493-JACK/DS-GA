package smartlibrary;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

class CsvRepository {
    private static final Path BOOKS_FILE = Paths.get("data", "books.csv");
    private static final Path LENDINGS_FILE = Paths.get("data", "lendings.csv");

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

    void saveBooks(List<Book> books) {
        ensureParentDirectory(BOOKS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(
                BOOKS_FILE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
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

    List<LendingRecord> loadLendingRecords() {
        ensureFileWithHeader(LENDINGS_FILE, "userName,userId,bookTitle,authorName,isbn,lendTime,lendPeriodDays,dueTime,returnTime");
        List<LendingRecord> records = new ArrayList<>();
        List<String> lines = readAllLinesSafe(LENDINGS_FILE);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] cols = parseCsvLine(line);
            if (cols.length != 8 && cols.length != 9) {
                throw new IllegalStateException("Invalid lending CSV row: " + line);
            }
            records.add(LendingRecord.fromCsvRow(cols));
        }
        return records;
    }

    void saveLendingRecords(List<LendingRecord> records) {
        ensureParentDirectory(LENDINGS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(
                LENDINGS_FILE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )) {
            writer.write("userName,userId,bookTitle,authorName,isbn,lendTime,lendPeriodDays,dueTime,returnTime");
            writer.newLine();
            for (LendingRecord record : records) {
                writer.write(toCsvLine(record.toCsvRow()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save lending CSV", e);
        }
    }

    private void ensureFileWithHeader(Path file, String header) {
        ensureParentDirectory(file);
        if (Files.exists(file)) {
            return;
        }
        try {
            Files.writeString(file, header + System.lineSeparator(), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create file: " + file, e);
        }
    }

    private void ensureParentDirectory(Path file) {
        try {
            Files.createDirectories(file.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create directory for: " + file, e);
        }
    }

    private List<String> readAllLinesSafe(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + file, e);
        }
    }

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

    private String escape(String value) {
        String safe = value == null ? "" : value;
        boolean needQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
        if (!needQuotes) {
            return safe;
        }
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }

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
