import com.example.space.virtualbooklibrary.Book;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;

import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;

import java.util.ArrayList;

import java.net.URLEncoder;

import java.text.NumberFormat;

/*
 Singleton class, handles communications with Google Books through it's API
 and returns the results as an ArrayList of Books
*/
public class APIHandler {

  private static APIHandler SINGLETON_HANDLER;
  private static final String APPLICATION_NAME = "Online_Library";
  private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
  private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();

  private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  private APIHandler() { }

  public static APIHandler getHandler() {
    if (SINGLETON_HANDLER == null)
      SINGLETON_HANDLER = new APIHandler();
    return SINGLETON_HANDLER;
  }

  // Formulates a proper query text given the query parameters.
  public String formulateQuery(String randomText, String bookTitle, String authorName, String ISBN) {
    StringBuilder sb = new StringBuilder();

    if (randomText != null && randomText.length() != 0) {
      String[] queryTokens = randomText.split(" ");
      sb.append(queryTokens[0]);
      for (int i = 1; i < queryTokens.length; i++) {
        sb.append('+');
        sb.append(queryTokens[i]);
      }
    }

    if (bookTitle != null && bookTitle.length() != 0) {
      if (sb.length() != 0) {
        sb.append(',');
      }
      sb.append("intitle:");
      String[] titleTokens = bookTitle.split(" ");
      sb.append(titleTokens[0]);
      for (int i = 1; i < titleTokens.length; i++) {
        sb.append('+');
        sb.append(titleTokens[i]);
      }
    }

    if (authorName != null && authorName.length() != 0) {
      if (sb.length() != 0) {
        sb.append(',');
      }
      sb.append("inauthor:");
      String[] authorTokens = authorName.split(" ");
      sb.append(authorTokens[0]);
      for (int i = 1; i < authorTokens.length; i++) {
        sb.append('+');
        sb.append(authorTokens[i]);
      }
    }

    if (ISBN != null && ISBN.length() != 0) {
      if (sb.length() != 0) {
        sb.append(',');
      }
      sb.append("isbn:");
      sb.append(ISBN);
    }

    return sb.toString();
  }

  public ArrayList<Book> queryGoogleBooks(String randomText, String bookTitle, String authorName, String ISBN) throws Exception {
    // Check that the application has proper credentials.
    ClientCredentials.errorIfNotSpecified();

    // Formulate a proper query given the sent parameters.
    String query = formulateQuery(randomText, bookTitle, authorName, ISBN);

    ArrayList<Book> searchResults = new ArrayList<Book>();

    // Set up Books client.
    final Books books = new Books.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory,
            null).setApplicationName(APPLICATION_NAME).setGoogleClientRequestInitializer(
            new BooksRequestInitializer(ClientCredentials.API_KEY)).build();

    // Set query string and filter only Google eBooks.
    System.out.println("Query: [" + query + "]");

    List volumesList = books.volumes().list(query);

    // Execute the query.
    Volumes volumes = volumesList.execute();
    if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
      System.out.println("No matches found.");
    }

    // Output results.
    for (Volume volume : volumes.getItems()) {
      int ratingsCount = 0;

      String Id = "", thumbnailLink = "", title = "", description = "", maturityRating = "",
              ratingStars = "", googleEbooksPrice = "", message = "", link = "", mainCategory = "";
      java.util.List<String> authors;
      java.util.List<String> categories;

      // Get the info about the give book.
      Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();

      // Categories.
      categories = volumeInfo.getCategories();

      // Main Category.
      mainCategory = volumeInfo.getMainCategory();

      // ID.
      Id = volume.getId();

      // Image link.
      if (volumeInfo.getImageLinks() != null) {
        thumbnailLink = volumeInfo.getImageLinks().getThumbnail();
      }

      // Title.
      title = volumeInfo.getTitle();

      // Author(s).
      authors = volumeInfo.getAuthors();

      // Maturity Rating.
      maturityRating = volumeInfo.getMaturityRating();

      // Description (if any).
      if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
        description = volumeInfo.getDescription();
      }

      // Ratings (if any).
      if (volumeInfo.getRatingsCount() != null && volumeInfo.getRatingsCount() > 0) {
        try {
          ratingsCount = volumeInfo.getRatingsCount();
          int fullRating = (int) Math.round(volumeInfo.getAverageRating().doubleValue());
          for (int i = 0; i < fullRating; ++i) {
            ratingStars += '*';
          }
        } catch (Exception e) { }
      }

      // Link to Google eBooks.
      link = volumeInfo.getInfoLink();
      searchResults.add(new Book(ratingsCount, Id, description, thumbnailLink, title,
              maturityRating, ratingStars, googleEbooksPrice, message, link, mainCategory, authors,
              categories));
    }

    System.out.println(volumes.getTotalItems()
            + " total results at http://books.google.com/ebooks?q=" + URLEncoder.encode(query, "UTF-8"));

    return searchResults;
  }

  public static void main(String[] args) {
    try {
      getHandler().queryGoogleBooks(""/* Random Text */
              , "game"/* Title */, ""/* Author */, ""/* ISBN */);
      return;
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}