# Smart Library System

Console-based Java library system for WIA1002 (Data Structure).

This project demonstrates:
- `Binary Search Tree (BST)` for catalogue storage and search by ISBN
- `Stack (LIFO)` for active borrowing history
- `CSV persistence` for books, lending records, and users

## Requirements
- JDK 17
- NetBeans (Ant project)

## Project Layout
- `src/smartlibrary/` : Java source code
- `data/books.csv` : available books in catalogue
- `data/lendings.csv` : lending transaction log
- `data/users.csv` : validated borrower identity mapping
- `build.xml`, `nbproject/` : NetBeans Ant build configuration

## How To Run
1. Open project folder in NetBeans.
2. Set project platform to JDK 17.
3. Click `Clean and Build`.
4. Run `smartlibrary.Main`.

## Console Menu
1. Add Book
2. View All Books
3. Search Book
4. Borrow Book
5. Return Latest Borrowed Book
6. Return Specific Borrowed Book (by ISBN)
7. View Lending Records
8. View Borrowing History
9. Exit

## Business Rules

### Borrowing
- Book must exist in catalogue.
- Lending period must be greater than 0.
- Borrower identity must already exist in `users.csv`.
- If `userName` + `userId` pair is wrong or not registered, borrowing is rejected.
- Wrong/unknown pairs are not saved to `users.csv`.

### Returning
- Uses LIFO stack rule (latest borrowed is returned first).
- Returned book is inserted back to catalogue BST.
- Matching active lending record is marked with `returnTime`.

## CSV Schemas

### `books.csv`
- `isbn,title,author`

### `lendings.csv`
Current schema (9 columns):
- `userName,userId,bookTitle,author,isbn,lendTime,lendPeriodDays,dueTime,returnTime`

Compatibility:
- Legacy 8-column rows (without `author`) are still supported.

### `users.csv`
- `userName,userId`

## Method Reference (Detailed)

### `Main` (UI layer)

#### `main(String[] args)`
Use:
- JVM entry point.
Mechanism:
- Instantiates `Main`.
- Calls `run()` to start menu loop.

#### `run()`
Use:
- Drives the full interactive console flow.
Mechanism:
- Prints app title once.
- Repeats menu loop until user chooses Exit.
- Reads integer choice via `readInt(...)`.
- Dispatches request to corresponding handler method.

#### `printMenu()`
Use:
- Displays all available menu options.
Mechanism:
- Prints static numbered options in user workflow order.

#### `handleAddBook()`
Use:
- Adds a new book into catalogue.
Mechanism:
- Reads ISBN, title, author.
- Calls `library.addBook(...)`.
- Prints success/failure based on duplicate ISBN detection.

#### `handleSearchBook()`
Use:
- Finds one book by ISBN.
Mechanism:
- Reads target ISBN.
- Calls `library.searchBook(isbn)`.
- Prints matched book in table format or not found message.

#### `handleViewAllBooks()`
Use:
- Shows current available catalogue.
Mechanism:
- Calls `library.getAllBooks()`.
- Prints rows in table format.
- Uses in-order BST result (already sorted by ISBN).

#### `handleBorrowBook()`
Use:
- Performs borrowing transaction with borrower validation.
Mechanism:
- Reads ISBN, borrower name, borrower ID, lending period.
- Checks whether ISBN exists first for accurate failure reason.
- Calls `library.borrowBook(...)`.
- Prints success or exact failure message (book not found vs borrower mismatch).

#### `handleReturnSpecificBorrowedBook()`
Use:
- Returns a chosen active borrowed book by ISBN.
Mechanism:
- Reads target ISBN.
- Calls `library.returnBookByIsbn(isbn)`.
- If successful, prints returned book with borrower name and ID.

#### `handleViewLendingRecords()`
Use:
- Displays lending history and active/returned status.
Mechanism:
- Calls `library.getLendingRecords()`.
- For each record, computes exceeded days using `getExceededDays(now)`.
- Prints status `ACTIVE` or `RETURNED` based on `returnTime`.

#### `handleReturnLatestBorrowed()`
Use:
- Returns most recent active borrowed book.
Mechanism:
- Calls `library.returnLatestBorrowed()` (stack pop logic).
- If returned, finds latest returned record for same ISBN.
- Displays returned book plus borrower name and ID.

#### `findLatestReturnedRecord(int isbn)`
Use:
- Resolves borrower info for just-returned item.
Mechanism:
- Reads all lending records from newest to oldest.
- Returns first record where ISBN matches and `returnTime != null`.

#### `readInt(String prompt)`
Use:
- Safe integer input utility.
Mechanism:
- Reads line from scanner.
- Retries until parse success.

#### `readPositiveInt(String prompt)`
Use:
- Integer input constrained to `> 0`.
Mechanism:
- Calls `readInt(...)`.
- Repeats until value is positive.

#### `readNonEmptyLine(String prompt)`
Use:
- Non-empty string input utility.
Mechanism:
- Reads line, trims spaces.
- Repeats until non-empty.

### `LibraryADT` (contract layer)

#### `addBook(int isbn, String title, String author)`
Use:
- Add unique book into catalogue.
Mechanism:
- Implemented by `SmartLibrary`; delegates to BST insert and persistence.

#### `getAllBooks()`
Use:
- Retrieve all available books.
Mechanism:
- Implemented as immutable in-order list of BST nodes.

#### `searchBook(int isbn)`
Use:
- Search by ISBN.
Mechanism:
- Recursive BST search.

#### `borrowBook(int isbn)`
Use:
- Backward-compatible borrow call.
Mechanism:
- Uses default borrower metadata and period through overloaded method.

#### `borrowBook(int isbn, String userName, String userId, int lendPeriodDays)`
Use:
- Full borrow operation with explicit borrower details.
Mechanism:
- Validates borrower mapping + lending days.
- Removes book from BST.
- Pushes book into stack.
- Appends lending record.
- Persists books and lendings.

#### `returnLatestBorrowed()`
Use:
- Return operation using LIFO rule.
Mechanism:
- Pops top stack node.
- Re-inserts book to BST.
- Marks latest active lending of same ISBN as returned.
- Persists books and lendings.

#### `returnBookByIsbn(int isbn)`
Use:
- Return a specific active borrowed book by ISBN.
Mechanism:
- Removes most recent matching ISBN from borrow stack.
- Re-inserts book to BST.
- Marks latest active lending of same ISBN as returned.
- Persists books and lendings.

#### `viewHistory()`
Use:
- Print active borrow stack.
Mechanism:
- Delegates to `BorrowHistoryStack.showAll()`.

#### `isCatalogueEmpty()`
Use:
- Quick availability check.
Mechanism:
- Delegates to `BookBST.isEmpty()`.

#### `getLendingRecords()`
Use:
- Read lending log.
Mechanism:
- Returns immutable view of in-memory list.

### `SmartLibrary` (core logic)

#### `SmartLibrary()`
Use:
- Initializes system state.
Mechanism:
- Calls `loadFromCsv()` at startup.

#### `addBook(...)`
Use:
- Add catalogue entry.
Mechanism:
- Inserts into BST.
- If success, persists books CSV.

#### `searchBook(...)`
Use:
- Lookup by ISBN.
Mechanism:
- Delegates to BST recursive search.

#### `getAllBooks()`
Use:
- Retrieve sorted catalogue.
Mechanism:
- Returns unmodifiable in-order list from BST.

#### `borrowBook(...)` (both overloads)
Use:
- Borrow flow.
Mechanism:
- Validates rules.
- Removes from BST.
- Pushes onto stack.
- Creates `LendingRecord` with `lendTime`, `dueTime`.
- Saves books + lendings.

#### `returnLatestBorrowed()`
Use:
- Return flow.
Mechanism:
- Pops stack top.
- Inserts back into BST.
- Marks latest active lending returned with current time.
- Saves books + lendings.

#### `returnBookByIsbn(int isbn)`
Use:
- Return a specific borrowed book.
Mechanism:
- Removes target ISBN from active stack.
- Inserts returned book back to catalogue.
- Marks matching active lending as returned.
- Saves books + lendings.

#### `loadFromCsv()`
Use:
- Full state hydration.
Mechanism:
- Loads books, lendings, users.
- Syncs missing users from existing lending records.
- Rebuilds active stack from lending records.

#### `persistBooks()`, `persistLendings()`, `persistUsers()`
Use:
- Save in-memory state to CSV files.
Mechanism:
- Delegates to `CsvRepository` save methods.

#### `markLatestActiveLendingReturned(int isbn)`
Use:
- Ensures the right record is closed on return.
Mechanism:
- Iterates lending records from end (newest first).
- Finds first matching ISBN with `returnTime == null`.
- Sets `returnTime = now`.

#### `rebuildBorrowHistoryFromRecords()`
Use:
- Reconstruct runtime stack after restart.
Mechanism:
- Filters active records (`returnTime == null`).
- Sorts by `lendTime` ascending.
- Pushes in that order so latest ends up at stack top.

#### `registerOrValidateUser(String userName, String userId)`
Use:
- Validate borrower identity against existing users.
Mechanism:
- Rejects conflicting name/id pairs.
- Accepts existing exact pair.
- Rejects unknown pair (no auto-registration).

#### `syncUsersFromLendingRecords()`
Use:
- Repair helper for older data.
Mechanism:
- Ensures every lending record user exists in `users.csv`.
- Adds missing pairs and persists once if changed.

### `BookBST` (catalogue data structure)

#### `insert(Book book)` + `insertRecursive(...)`
Use:
- Inserts by ISBN ordering.
Mechanism:
- Recursively moves left/right by key comparison.
- Rejects duplicate ISBN.

#### `search(int isbn)` + `searchRecursive(...)`
Use:
- Finds a book quickly by key.
Mechanism:
- Recursive BST traversal using key comparisons.

#### `remove(int isbn)` + `removeRecursive(...)`
Use:
- Removes a book from catalogue.
Mechanism:
- Handles BST delete cases:
- no child / one child direct replacement
- two children replacement with in-order successor

#### `toInOrderList()`
Use:
- Exports sorted books.
Mechanism:
- In-order traversal (left, root, right).

#### `isEmpty()`
Use:
- Checks if catalogue has no nodes.
Mechanism:
- Returns whether root is `null`.

### `BorrowHistoryStack`

#### `push(Book book)`
Use:
- Tracks new active borrow.
Mechanism:
- Pushes book onto stack top.

#### `pop()`
Use:
- Retrieves most recent active borrow.
Mechanism:
- Pops top element; returns `null` if empty.

#### `removeByIsbn(int isbn)`
Use:
- Removes a specific active borrowed book from stack.
Mechanism:
- Scans stack from top to bottom.
- Removes first matched ISBN (most recent match).
- Returns removed book, or `null` if not found.

#### `clear()`
Use:
- Startup rebuild support.
Mechanism:
- Empties stack entirely.

#### `isEmpty()`
Use:
- Checks active history state.
Mechanism:
- Returns stack emptiness.

#### `showAll()`
Use:
- Displays current stack in table format.
Mechanism:
- Iterates stack from top to bottom and prints rows.

### `CsvRepository`

#### `loadBooks()`, `saveBooks(...)`
Use:
- Read/write `books.csv`.
Mechanism:
- Ensures file/header exists.
- Parses/serializes CSV rows.

#### `loadLendingRecords()`, `saveLendingRecords(...)`
Use:
- Read/write `lendings.csv`.
Mechanism:
- Supports 8 or 9 columns on load.
- Writes standard 9-column schema on save.

#### `loadUsers()`, `saveUsers(...)`
Use:
- Read/write `users.csv`.
Mechanism:
- Loads validated user pairs, persists updates.

#### `ensureFileWithHeader(...)`
Use:
- File bootstrap.
Mechanism:
- Creates missing file and writes header once.

#### `ensureParentDirectory(...)`
Use:
- Prevent path errors before writing.
Mechanism:
- Creates parent directories if missing.

#### `readAllLinesSafe(...)`
Use:
- Centralized file read with runtime exception conversion.
Mechanism:
- Wraps `Files.readAllLines(...)`.

#### `toCsvLine(...)`, `escape(...)`, `parseCsvLine(...)`
Use:
- CSV serialization/parsing helpers.
Mechanism:
- Escapes commas, quotes, newlines.
- Handles quoted values and escaped quotes while parsing.

### `LendingRecord`

#### Constructor + getters
Use:
- Immutable snapshot of borrowing event (except return time).
Mechanism:
- Stores borrower details, book snapshot, and lending timeline.

#### `markReturned(LocalDateTime time)`
Use:
- Marks record as completed.
Mechanism:
- Sets `returnTime` value.

#### `getExceededDays(LocalDateTime referenceTime)`
Use:
- Computes overdue days.
Mechanism:
- Compares due time against return time (or now if active).
- Returns 0 when not overdue.

#### `toCsvRow()`
Use:
- Prepare row for CSV save.
Mechanism:
- Formats date-time fields as ISO strings.

#### `fromCsvRow(String[] cols)`
Use:
- Parse CSV row into object.
Mechanism:
- Accepts 9-column (current) and 8-column (legacy) formats.
- Uses `author = "Unknown"` for legacy rows.

### `Book`, `BookNode`, `LibraryUser`

#### `Book`
Use:
- Immutable domain model for book data.
Mechanism:
- Stores ISBN, title, author; used in BST and stack.

#### `BookNode`
Use:
- Internal BST node wrapper.
Mechanism:
- Holds one `Book` and left/right child references.

#### `LibraryUser`
Use:
- Name-ID mapping object.
Mechanism:
- Stores one validated borrower identity pair for consistency checks.

## Persistence Lifecycle
1. Startup: load all CSV files.
2. Runtime: update in-memory BST/stack/list structures.
3. On write operations: persist affected CSV files immediately.
4. Restart-safe: rebuild active stack from lending records.

## Troubleshooting
- If latest code changes are not reflected, run `Clean and Build`.
- If CSV error appears, check:
- header row is correct
- data row column count matches documented schema
- date-time fields are valid ISO format
