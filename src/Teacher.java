public class Teacher extends User {
    private String designation;
    private static final int MAX_BORROW = 5;

    public Teacher(String id, String name, String desig) {
        super(id, name);
        this.designation = desig;
    }

    @Override
    public void borrowBook(Book book) throws LibraryException {
        if (borrowedBooks.size() >= MAX_BORROW) {
            throw new MaxBorrowLimitReachedException(
                    "Teacher borrow limit reached (" + MAX_BORROW + ").");
        }
        if (!book.borrowCopy()) {
            throw new BookNotAvailableException("No copies left.");
        }
        borrowedBooks.add(book);
        System.out.println(name + " borrowed \"" + book.getTitle() + "\"");
    }
}
