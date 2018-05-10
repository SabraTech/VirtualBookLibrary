public class Pair {
    String bookID, bookTitle;

    public Pair(String bookID, String bookTitle) {
        this.bookID = bookID;
        this.bookTitle = bookTitle;
    }

    public String toString() {
        return "{id=" + bookID + ", title=" + bookTitle + "}";
    }
}