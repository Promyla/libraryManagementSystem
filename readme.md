
# LibrarySystem

**A Java-Based Library Management System**

**Author:** Rahima Rahman Promy  
**Student ID:** 2024-3-60-776  
**Course ID:** CSE110  
**Course Name:** Object Oriented Programming  
**Date:** June 1, 2025  

---

## Table of Contents

1. [Project Overview](#project-overview)  
2. [Features](#features)  
3. [Class Design](#class-design)  
   - [Book](#book)  
   - [User (Abstract Class)](#user-abstract-class)  
   - [Student](#student)  
   - [Teacher](#teacher)  
   - [Borrowable Interface](#borrowable-interface)  
   - [Library](#library)  
   - [UserManager](#usermanager)  
4. [Exception Handling](#exception-handling)  
5. [Excel File Integration](#excel-file-integration)  
6. [Main Application Flow (Main.java)](#main-application-flow-mainjava)  
7. [Summary of Core Concepts](#summary-of-core-concepts)  
   - [Object-Oriented Design](#object-oriented-design)  
   - [Exception Handling](#exception-handling-1)  
   - [Excel Reading & Writing](#excel-reading--writing)  
8. [How to Compile & Run](#how-to-compile--run)  
9. [Project Structure](#project-structure)  
10. [Contributing](#contributing)  
11. [License](#license)  

---

## Project Overview

LibrarySystem is a console-based Java application that simulates a basic library management system. It demonstrates:

- **Object-Oriented Principles:** Classes, inheritance, interfaces, encapsulation  
- **Custom Exception Handling:** Distinct exception classes for various error conditions  
- **Excel File Integration:** Reading from and writing to `.xlsx` files via Apache POI  

Users (Students and Teachers) can authenticate, view available books, borrow and return books, and track their own borrowed items. Book and user data are persisted in Excel spreadsheets (`books.xlsx`, `students.xlsx`, `teachers.xlsx`).

---

## Features

- Load book inventory from `books.xlsx`  
- Load user data (students/teachers) from `students.xlsx` and `teachers.xlsx`  
- Authenticate by user ID  
- List all available books and their remaining copies  
- Borrow a book (with role-specific borrowing limits)  
- Return a borrowed book  
- View currently borrowed books for the logged-in user  
- Persist changes to `books.xlsx` after each borrow/return operation  
- Custom exception classes for clear, user-friendly error messages  

---

## Class Design

### Book

- **Fields:**  
  - `String bookId` — unique identifier  
  - `String title` — book title  
  - `String author` — book author  
  - `int availableCopies` — number of copies currently available  

- **Constructors:**  
  - Default: Initializes placeholder book with zero copies  
  - Parameterized: `(String bookId, String title, String author, int copies)`  

- **Key Methods:**  
  - `boolean borrowCopy()` — decrements `availableCopies` if > 0; returns `true` on success, `false` if none left  
  - `void returnCopy()` — increments `availableCopies` by 1  
  - `void displayInfo()` — prints book details and remaining copies  
  - Getters for all fields  

### User (Abstract Class)

- **Fields (protected):**  
  - `String userId` — unique identifier  
  - `String name` — full name  
  - `List<Book> borrowedBooks` — books currently borrowed by this user  

- **Constructor:**  
  - `(String userId, String name)` — initializes `borrowedBooks` as empty  

- **Abstract Method:**  
  - `void borrowBook(Book book)` throws `LibraryException` — to be implemented by subclasses  

- **Concrete Methods:**  
  - `void returnBook(Book book)` — checks if `book` is in `borrowedBooks`; if so, calls `book.returnCopy()` and removes it from the list; otherwise prints a message  
  - `void viewBorrowedBooks()` — iterates over `borrowedBooks` and calls `displayInfo()` on each; prints a notice if none  

### Student

- **Extends:** `User`  
- **Additional Field:**  
  - `String department` (e.g., CSE, EEE, BBA)  
- **Borrowing Limit:** `private static final int MAX_BORROW = 3`  
- **Constructor:**  
  - `(String userId, String name, String department)`  
- **Implementation of `borrowBook(Book book)`:**  
  1. If `borrowedBooks.size() >= MAX_BORROW`, throw `MaxBorrowLimitReachedException("Student borrow limit reached (3).")`  
  2. Call `book.borrowCopy()`; if it returns `false`, throw `BookNotAvailableException("No copies left.")`  
  3. Otherwise, add `book` to `borrowedBooks` and print a confirmation message  

### Teacher

- **Extends:** `User`  
- **Additional Field:**  
  - `String designation` (e.g., Professor, Lecturer, Asst. Professor)  
- **Borrowing Limit:** `private static final int MAX_BORROW = 5`  
- **Constructor:**  
  - `(String userId, String name, String designation)`  
- **Implementation of `borrowBook(Book book)`:**  
  1. If `borrowedBooks.size() >= MAX_BORROW`, throw `MaxBorrowLimitReachedException("Teacher borrow limit reached (5).")`  
  2. Call `book.borrowCopy()`; if it returns `false`, throw `BookNotAvailableException("No copies left.")`  
  3. Otherwise, add `book` to `borrowedBooks` and print a confirmation message  

### Borrowable Interface

```java
public interface Borrowable {
    void borrowBook(Book book) throws LibraryException;
}
````

* Requires any implementing class (e.g., `Student`, `Teacher`) to define `borrowBook(Book)` and propagate `LibraryException` on failure.

### Library

* **Fields (private):**

  * `List<Book> bookList` — holds all `Book` objects in memory
  * `String bookFilePath` — path to `books.xlsx` for saving updates

* **Key Methods:**

  * `void loadBooksFromExcel(String filePath) throws IOException`

    1. Open `FileInputStream` → `XSSFWorkbook`
    2. Read the first sheet, skip header row
    3. For each data row, parse:

       * `String id = getStringValue(cell0)`
       * `String title = getStringValue(cell1)`
       * `String author = getStringValue(cell2)`
       * `int copies = (int)cell3.getNumericCellValue()`
    4. Create `new Book(id, title, author, copies)` and add to `bookList`
    5. Store `bookFilePath = filePath`
  * `void saveBooksToExcel() throws IOException`

    1. If `bookFilePath == null`, return immediately
    2. Create `XSSFWorkbook` and a sheet named “Books”
    3. Write header row: `Book ID`, `Title`, `Author`, `Copies`
    4. For each `Book b : bookList`, create a row and write its fields
    5. Open `FileOutputStream` → write the workbook back to `bookFilePath`
  * `Book getBookById(String id)` — searches `bookList` for a matching `bookId`; returns `null` if not found
  * `void displayAvailableBooks()` — iterates over `bookList` and calls `displayInfo()` on each

* **Helper Method:**

  * `String getStringValue(Cell cell)` — returns a string representation for any cell type (STRING, NUMERIC, BOOLEAN, FORMULA)

### UserManager

* **Fields (private):**

  * `List<User> userList` — holds both `Student` and `Teacher` objects

* **Key Methods:**

  * `void loadUsersFromExcel(String filePath, String userType) throws IOException`

    1. Open `FileInputStream` → `XSSFWorkbook`
    2. Read the first sheet, skip header row
    3. For each data row, parse:

       * `String id = getStringValue(cell0)`
       * `String name = getStringValue(cell1)`
       * `String third = getStringValue(cell2)` (department or designation)
    4. If `userType.equalsIgnoreCase("student")`, instantiate `new Student(id, name, third)`
       Else if `userType.equalsIgnoreCase("teacher")`, instantiate `new Teacher(id, name, third)`
    5. Add the new `User` object to `userList`
  * `User authenticateUser(String id) throws UserNotFoundException`

    * Search `userList` for matching `userId`; if found, return it; otherwise throw `new UserNotFoundException("User not found.")`

---

## Exception Handling

All custom exceptions extend `LibraryException`, which itself extends `Exception`.

```java
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
```

* **UserNotFoundException**

  ```java
  public class UserNotFoundException extends LibraryException {
      public UserNotFoundException(String message) {
          super(message);
      }
  }
  ```

  Thrown when `UserManager.authenticateUser(...)` fails to locate a matching ID.

* **BookNotAvailableException**

  ```java
  public class BookNotAvailableException extends LibraryException {
      public BookNotAvailableException(String message) {
          super(message);
      }
  }
  ```

  Thrown when a user attempts to borrow a book whose `availableCopies == 0`.

* **MaxBorrowLimitReachedException**

  ```java
  public class MaxBorrowLimitReachedException extends LibraryException {
      public MaxBorrowLimitReachedException(String message) {
          super(message);
      }
  }
  ```

  Thrown when a user’s `borrowedBooks.size()` exceeds their role’s maximum (`3` for students, `5` for teachers).

In `Main.java`, calls to `borrowBook(...)`, `returnBook(...)`, and `authenticateUser(...)` are wrapped in `try/catch` blocks to present user-friendly messages without crashing.

---

## Excel File Integration

Excel I/O is handled via **Apache POI** (`org.apache.poi.ss.usermodel.*` and `org.apache.poi.xssf.usermodel.XSSFWorkbook`).

### Reading from Excel

Both `Library.loadBooksFromExcel(String filePath)` and `UserManager.loadUsersFromExcel(String filePath, String userType)` follow the same pattern:

1. Open `FileInputStream fis = new FileInputStream(filePath)`
2. `XSSFWorkbook wb = new XSSFWorkbook(fis)`
3. `Sheet sheet = wb.getSheetAt(0)` (assumes data is on the first sheet)
4. Skip the header row (boolean flag)
5. For each subsequent `Row row : sheet`, read cells using a helper:

   ```java
   private String getStringValue(Cell cell) {
       switch (cell.getCellType()) {
           case STRING:
               return cell.getStringCellValue();
           case NUMERIC:
               return String.valueOf((int) cell.getNumericCellValue());
           case BOOLEAN:
               return String.valueOf(cell.getBooleanCellValue());
           case FORMULA:
               return cell.getCellFormula();
           default:
               return "";
       }
   }
   ```
6. Instantiate the appropriate object (`Book`, `Student`, or `Teacher`) and add to the in-memory list
7. The `try-with-resources` block auto-closes `Workbook` and `FileInputStream`

### Writing to Excel

Only `Library.saveBooksToExcel()` writes back to `books.xlsx`:

1. If `bookFilePath == null`, return immediately
2. Create a new `XSSFWorkbook wb = new XSSFWorkbook()`
3. `Sheet sheet = wb.createSheet("Books");`
4. Write the header row (cells: "Book ID", "Title", "Author", "Copies")
5. For each `Book b : bookList`, create a new `Row` and fill cells with `b.getBookId()`, `b.getTitle()`, `b.getAuthor()`, and `b.getAvailableCopies()`
6. `FileOutputStream fos = new FileOutputStream(bookFilePath)` and `wb.write(fos)`
7. The `try-with-resources` block auto-closes `Workbook` and `FileOutputStream`

This approach ensures that any change to book availability (after borrow/return) is immediately persisted to `books.xlsx`.

---

## Main Application Flow (Main.java)

1. **Initialization**

   ```java
   Scanner sc = new Scanner(System.in);
   Library lib = new Library();
   UserManager um = new UserManager();
   ```

   * Prints out the location of the Commons IO JAR to confirm dependencies:

     ```java
     System.out.println("Commons IO loaded from: " +
         org.apache.commons.io.input.BoundedInputStream.class
             .getProtectionDomain()
             .getCodeSource()
             .getLocation());
     ```

2. **Load Data**

   ```java
   try {
       lib.loadBooksFromExcel("books.xlsx");
       um.loadUsersFromExcel("students.xlsx", "student");
       um.loadUsersFromExcel("teachers.xlsx", "teacher");
   } catch (IOException e) {
       System.out.println("Couldn’t read Excel files: " + e.getMessage());
       return;  // terminate if files are missing or unreadable
   }
   ```

3. **User Authentication Loop**

   ```java
   User current = null;
   while (true) {
       System.out.print("Enter your user ID: ");
       String uid = sc.nextLine().trim();
       try {
           current = um.authenticateUser(uid);
           break;  // successful login
       } catch (UserNotFoundException e) {
           System.out.println(e.getMessage());
           // prompt again
       }
   }
   ```

4. **Main Menu Loop**

   ```java
   while (true) {
       System.out.println("\n1) List Books    2) Borrow    3) Return    4) My Books    5) Exit");
       System.out.print("> ");
       String choice = sc.nextLine().trim();
       switch (choice) {
           case "1":
               lib.displayAvailableBooks();
               break;
           case "2":
               System.out.print("Book ID to borrow: ");
               String borrowId = sc.nextLine().trim();
               handleBorrowOrReturn(borrowId, current, lib, true);
               break;
           case "3":
               System.out.print("Book ID to return: ");
               String returnId = sc.nextLine().trim();
               handleBorrowOrReturn(returnId, current, lib, false);
               break;
           case "4":
               current.viewBorrowedBooks();
               break;
           case "5":
               try {
                   lib.saveBooksToExcel();  // persist final state
               } catch (IOException e) {
                   System.out.println("Error saving books: " + e.getMessage());
               }
               System.out.println("Goodbye!");
               sc.close();
               return;
           default:
               System.out.println("Invalid choice.");
       }
   }
   ```

5. **Helper Method: `handleBorrowOrReturn`**

   ```java
   private static void handleBorrowOrReturn(String id, User current, Library lib, boolean isBorrow) {
       Book book = lib.getBookById(id);
       if (book == null) {
           System.out.println("Book not found.");
           return;
       }
       try {
           if (isBorrow) {
               current.borrowBook(book);
           } else {
               current.returnBook(book);
           }
           lib.saveBooksToExcel();
       } catch (LibraryException | IOException e) {
           System.out.println(e.getMessage());
       }
   }
   ```

   * Handles both borrowing and returning logic
   * Catches `LibraryException` (custom exceptions) and `IOException` (Excel I/O)

---

## Summary of Core Concepts

### Object-Oriented Design

* **Encapsulation:** Each class—`Book`, `User`, `Student`, `Teacher`, `Library`, `UserManager`—encapsulates its own data and behavior. Fields are private or protected; public methods provide controlled access.
* **Inheritance & Polymorphism:** `Student` and `Teacher` extend the abstract `User` class and override `borrowBook(...)` to provide role-specific logic. Any class implementing `Borrowable` can be used polymorphically for borrowing.
* **Abstraction:** `User` abstracts common functionality (returning and viewing borrowed books) while deferring borrowing logic to subclasses. `Library` abstracts all Excel I/O away from the rest of the application.

### Exception Handling

* **Custom Exceptions:**

  * `LibraryException` (base)
  * `UserNotFoundException`
  * `BookNotAvailableException`
  * `MaxBorrowLimitReachedException`

  Each exception carries a descriptive message. Methods that can fail declare `throws LibraryException`, forcing callers to catch or propagate, which prevents silent failures.

* **Try-With-Resources:**

  * All Excel read/write operations use `try (FileInputStream fis = …; Workbook wb = …)` or `try (Workbook wb = …; FileOutputStream fos = …)` to auto-close resources and avoid leaks.

### Excel Reading & Writing

* **Apache POI Integration:**

  * Reading: open `FileInputStream` → `XSSFWorkbook` → iterate `Sheet` rows → parse cells with a helper (`getStringValue(...)`).
  * Writing: create new `XSSFWorkbook` → create `Sheet` → write a header row → write one row per `Book` → use `FileOutputStream` to save back to the same file.

* **Data Persistence:**

  * Borrow/return operations immediately call `saveBooksToExcel()`, so if the application stops and restarts, `books.xlsx` reflects the latest inventory.

---

## How to Compile & Run

1. **Prerequisites**

   * Java 8 or higher
   * Apache POI (add `poi-*.jar` and `poi-ooxml-*.jar` to your classpath)
   * Place `books.xlsx`, `students.xlsx`, and `teachers.xlsx` in the same directory as your compiled classes

2. **Compile**

   ```bash
   javac -cp ".;path/to/poi.jar;path/to/poi-ooxml.jar" src/*.java
   ```

   Replace `;` with `:` on Unix-based systems and update the POI JAR paths accordingly.

3. **Run**

   ```bash
   java -cp ".;path/to/poi.jar;path/to/poi-ooxml.jar" Main
   ```

   The console will prompt for user input:

   1. Enter your user ID (from `students.xlsx` or `teachers.xlsx`)
   2. Choose from the menu:

      * List Books
      * Borrow
      * Return
      * My Books
      * Exit

---

## Project Structure

```
LibrarySystem/
├── books.xlsx
├── students.xlsx
├── teachers.xlsx
├── src/
│   ├── Book.java
│   ├── Borrowable.java
│   ├── Library.java
│   ├── User.java
│   ├── Student.java
│   ├── Teacher.java
│   ├── UserManager.java
│   ├── LibraryException.java
│   ├── UserNotFoundException.java
│   ├── BookNotAvailableException.java
│   ├── MaxBorrowLimitReachedException.java
│   └── Main.java
└── README.md
```

* **`books.xlsx`**: Contains book inventory (Book ID, Title, Author, Copies)
* **`students.xlsx`**: Contains student data (Student ID, Name, Department, Total Borrowed)
* **`teachers.xlsx`**: Contains teacher data (Teacher ID, Name, Designation, Total Borrowed)
* **`src/`**: All Java source files (classes and exception definitions)

---

## Contributing

1. Fork this repository.
2. Create a new branch:

   ```bash
   git checkout -b feature/YourFeature
   ```
3. Make your changes and ensure all existing functionality still works.
4. Run all tests (if any) and verify there are no compilation errors.
5. Commit your changes:

   ```bash
   git commit -m "Add <describe your feature>"
   ```
6. Push to your fork:

   ```bash
   git push origin feature/YourFeature
   ```
7. Open a pull request with a clear description of your changes.

---

## License

This project is released under the [MIT License](LICENSE).

---

> **Note:**
>
> * Update the Excel files (`books.xlsx`, `students.xlsx`, `teachers.xlsx`) as needed before running.
> * If you encounter any issues reading or writing to Excel, ensure that the files are in `.xlsx` format (not `.xls`) and that no other program is locking them.
> * For any questions, please open an issue or contact the author.

```
```
