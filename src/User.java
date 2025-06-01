import java.util.ArrayList;
import java.util.List;

public abstract class User implements Borrowable {
    protected String userId;
    protected String name;
    protected List<Book> borrowedBooks = new ArrayList<>();

    public User(String userId, String name) {
        this.userId = userId;
        this.name   = name;
    }

    public abstract void borrowBook(Book book) throws LibraryException;

    public void returnBook(Book book) {
        if (borrowedBooks.remove(book)) {
            book.returnCopy();
            System.out.println(name + " returned \"" + book.getTitle() + "\"");
        } else {
            System.out.println("You didn’t borrow that book.");
        }
    }

    public void viewBorrowedBooks() {
        if (borrowedBooks.isEmpty()) {
            System.out.println("No books borrowed.");
            return;
        }
        System.out.println("Borrowed by " + name + ":");
        borrowedBooks.forEach(Book::displayInfo);
    }

    public String getUserId() { return userId; }
}
