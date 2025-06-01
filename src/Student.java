public class Student extends User {
    private String department;
    private static final int MAX_BORROW = 3;

    public Student(String id, String name, String dept) {
        super(id, name);
        this.department = dept;
    }

    @Override
    public void borrowBook(Book book) throws LibraryException {
        if (borrowedBooks.size() >= MAX_BORROW) {
            throw new MaxBorrowLimitReachedException(
                    "Student borrow limit reached (" + MAX_BORROW + ").");
        }
        if (!book.borrowCopy()) {
            throw new BookNotAvailableException("No copies left.");
        }
        borrowedBooks.add(book);
        System.out.println(name + " borrowed \"" + book.getTitle() + "\"");
    }
}
