import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class BookTest {

  @Test
  void test() {
    String mainCategory = "";
    String id = "72aHpwAACAAJ";
    String thumbnailLink = "http://books.google.com/books/content?id=72aHpwAACAAJ&printsec=frontcover&img=1&zoom=1&source=gbs_api";
    String title = "A Game of Thrones";
    String maturityRating = "NOT_MATURE";
    String description = "Now available in a specially priced edition--the first volume in an epic series by a master of contemporary fantasy, filled with mystery, intrigue, romance, and adventure. Reissue.";
    String userRatingStars = "****";
    String price = "244$";
    String message = "Additional information about this book is available from Google eBooks at:\n";
    String link = "http://books.google.com.eg/books?id=i_SorqUvsOEC&dq=intitle:Game%2Bof%2Bthrones,inauthor:George&hl=&source=gbs_api\n";
    int ratingsCount = 2559;
    ArrayList<String> authorsList = new ArrayList<String>();
    ArrayList<String> categoryList = new ArrayList<String>();
    authorsList.add("George R.R Martin");
    categoryList.add("Fantasy");
    categoryList.add("fiction");
    Book book = new Book(ratingsCount, id, description, thumbnailLink, title, maturityRating,
        userRatingStars, price, message, link, mainCategory, authorsList, categoryList);
    assertEquals(id, book.getId());
    assertEquals(mainCategory, book.getMainCategory());
    assertEquals(thumbnailLink, book.getThumbnailLink());
    assertEquals(title, book.getTitle());
    assertEquals(maturityRating, book.getMaturityRating());
    assertEquals(description, book.getDescription());
    assertEquals(userRatingStars, book.getRatingStars());
    assertEquals(price, book.getGoogleEbooksPrice());
    assertEquals(message, book.getMessage());
    assertEquals(link, book.getLink());
    assertEquals(ratingsCount, book.getRatingsCount());
    assertEquals(authorsList, book.getAuthors());
    assertEquals(categoryList, book.getCategories());
  }

}
