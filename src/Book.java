public class Book {
    private String bookId;
    private String title;
    private String author;
    private int availableCopies;

    public Book() {
        this("0", "Untitled", "Unknown", 0);
    }

    public Book(String bookId, String title, String author, int copies) {
        this.bookId = bookId;
        this.title  = title;
        this.author = author;
        this.availableCopies = copies;
    }

    public void displayInfo() {
        System.out.println(bookId + " | " + title + " | " + author +
                " | copies left: " + availableCopies);
    }

    public boolean borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public void returnCopy() {
        availableCopies++;
    }

    public String getBookId()           { return bookId; }
    public String getTitle()            { return title; }
    public String getAuthor()           { return author; }
    public int    getAvailableCopies()  { return availableCopies; }
}
