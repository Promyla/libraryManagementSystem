import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library lib = new Library();
        UserManager um = new UserManager();
        System.out.println("Commons IO loaded from: " +
                org.apache.commons.io.input.BoundedInputStream.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation());
        try {
            lib.loadBooksFromExcel("books.xlsx");
            um.loadUsersFromExcel("students.xlsx", "student");
            um.loadUsersFromExcel("teachers.xlsx", "teacher");
        } catch (IOException e) {
            System.out.println("❌ Couldn’t read Excel files: " + e.getMessage());
            return;
        }

        User current;
        while (true) {
            System.out.print("Enter your user ID: ");
            String uid = sc.nextLine().trim();
            try {
                current = um.authenticateUser(uid);
                break;
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            System.out.println("\n1) List Books  2) Borrow  3) Return  " +
                    "4) My Books  5) Exit");
            System.out.print("> ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    lib.displayAvailableBooks();
                    break;

                case "2":
                    System.out.print("Book ID to borrow: ");
                    handleBorrowOrReturn(sc.nextLine(), current, lib, true);
                    break;

                case "3":
                    System.out.print("Book ID to return: ");
                    handleBorrowOrReturn(sc.nextLine(), current, lib, false);
                    break;

                case "4":
                    current.viewBorrowedBooks();
                    break;

                case "5":
                    try { lib.saveBooksToExcel(); }
                    catch (IOException ex) {
                        System.out.println("⚠️ Could not save books file.");
                    }
                    System.out.println("Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void handleBorrowOrReturn(String id,
                                             User current,
                                             Library lib,
                                             boolean isBorrow) {
        Book book = lib.getBookById(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        try {
            if (isBorrow)  current.borrowBook(book);
            else           current.returnBook(book);

            lib.saveBooksToExcel();
        } catch (LibraryException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
