
/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;

/* Singleton class, handles communications with Google Books through it's API
 *  and returns the results as an ArrayList of Books */
public class APIHandler {

    private static final String APPLICATION_NAME = "Online_Library";
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
    private static APIHandler SINGLETON_HANDLER;

    private JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    private APIHandler() {

    }

    public static APIHandler getHandler() {
        if (SINGLETON_HANDLER == null)
            SINGLETON_HANDLER = new APIHandler();
        return SINGLETON_HANDLER;
    }

    // Queries GoogleBooks API and returns an ArrayList of Books as a result.
    // returns a maximum of 10 books per call.
    // randomText: search for books with some keyword.
    // bookTitle: search for books with some title.
    // authorName: search for books of some author.
    // ISBN: search for a book with some ISBN.
    // page: the page to begin the search with (0, 1, ..) each of this pages
    // contains 10 results except for the last page.
    // String user: the user that generated this query, used to improve
    // suggestions, can be null.
    // getASingleBook: the call returns only 1 book in case this is set to true.
    public ArrayList<Book> queryGoogleBooks(String randomText, String bookTitle, String authorName,
                                            String ISBN, int page, String user, boolean getASingleBook) throws Exception {
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
        // volumesList.setFilter("ebooks");
        volumesList.setStartIndex((long) page * 10);
        volumesList.setMaxResults(10l);

        // Execute the query.
        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            System.out.println("No matches found.");
            return new ArrayList<Book>();
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
            Volume.SaleInfo saleInfo = volume.getSaleInfo();
            System.out.println("==========");
            // Categories.
            categories = volumeInfo.getCategories();
            if (categories != null)
                System.out.println("categories = " + categories.toString());
            // Main Category.
            mainCategory = volumeInfo.getMainCategory();
            System.out.println("Main Category = " + mainCategory);
            // ID.
            Id = volume.getId();
            System.out.println(volume.getId());
            // Image link.
            if (volumeInfo.getImageLinks() != null) {
                thumbnailLink = volumeInfo.getImageLinks().getThumbnail();
                System.out.println(volumeInfo.getImageLinks().getThumbnail());
            }
            // Title.
            title = volumeInfo.getTitle();
            System.out.println("Title: " + volumeInfo.getTitle());
            // Author(s).
            authors = volumeInfo.getAuthors();
            if (authors != null && !authors.isEmpty()) {
                System.out.print("Author(s): ");
                for (int i = 0; i < authors.size(); ++i) {
                    System.out.print(authors.get(i));
                    if (i < authors.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
            // Maturity Rating.
            maturityRating = volumeInfo.getMaturityRating();
            System.out.println(volumeInfo.getMaturityRating());
            // Description (if any).
            if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                System.out.println("Description: " + volumeInfo.getDescription());
                description = volumeInfo.getDescription();
            }
            // Ratings (if any).

            if (volumeInfo.getRatingsCount() != null && volumeInfo.getRatingsCount() > 0) {
                try {
                    ratingsCount = volumeInfo.getRatingsCount();
                    int fullRating = (int) Math.round(volumeInfo.getAverageRating().doubleValue());
                    System.out.print("User Rating: ");
                    for (int i = 0; i < fullRating; ++i) {
                        ratingStars += '*';
                        System.out.print("*");
                    }
                    System.out.println(" (" + volumeInfo.getRatingsCount() + " rating(s))");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Price (if any).
            if (saleInfo != null && "FOR_SALE".equals(saleInfo.getSaleability())) {
                try {
                    double save = saleInfo.getListPrice().getAmount() - saleInfo.getRetailPrice().getAmount();
                    if (save > 0.0) {
                        System.out.print("List: " + CURRENCY_FORMATTER.format(saleInfo.getListPrice()
                                .getAmount()) + "  ");
                    }
                    googleEbooksPrice = CURRENCY_FORMATTER.format(saleInfo.getRetailPrice().getAmount()
                            .toString());
                    System.out.print("Google eBooks Price: " + CURRENCY_FORMATTER.format(saleInfo
                            .getRetailPrice().getAmount()));
                    if (save > 0.0) {
                        System.out.print("  You Save: " + CURRENCY_FORMATTER.format(save) + " ("
                                + PERCENT_FORMATTER.format(save / saleInfo.getListPrice().getAmount()) + ")");
                    }
                    System.out.println();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            // Access status.
            String accessViewStatus = volume.getAccessInfo().getAccessViewStatus();
            message = "Additional information about this book is available from Google eBooks at:";
            if ("FULL_PUBLIC_DOMAIN".equals(accessViewStatus)) {
                message = "This public domain book is available for free from Google eBooks at:";
            } else if ("SAMPLE".equals(accessViewStatus)) {
                message = "A preview of this book is available from Google eBooks at:";
            }
            System.out.println(message);
            // Link to Google eBooks.
            link = volumeInfo.getInfoLink();
            System.out.println(volumeInfo.getInfoLink());
            searchResults.add(new Book(ratingsCount, Id, description, thumbnailLink, title,
                    maturityRating, ratingStars, googleEbooksPrice, message, link, mainCategory, authors,
                    categories));
            if (user != null) {
                UserDatabaseHandler.getHandler().addHistory(user, Id, title);
            }
            if (getASingleBook)
                break;
        }
        System.out.println("==========");
        System.out.println(volumes.getTotalItems()
                + " total results at http://books.google.com/ebooks?q=" + URLEncoder.encode(query,
                "UTF-8"));
        System.out.println(searchResults.size() + " results.");
        return searchResults;

    }

    // left for backward compatibility.
    public ArrayList<Book> queryGoogleBooks(String randomText, String bookTitle, String authorName,
                                            String ISBN) throws Exception {
        return queryGoogleBooks(randomText, bookTitle, authorName, ISBN, 0, null, false);

    }

    public ArrayList<Book> getBooksWithIDs(ArrayList<Pair> IDs) throws Exception {
        ArrayList<Book> result = new ArrayList<Book>();
        for (Pair IDAndTitle : IDs)
            result.addAll(queryGoogleBooks(IDAndTitle.bookID // keyword.
                    , IDAndTitle.bookTitle// title.
                    , "" // author.
                    , "" // isbn.
                    , 0 // page.
                    , null // user.
                    , true // get a single book.
            ));
        System.out.println(result.size());
        return result;
    }

    // Formulates a proper query text given the query parameters.
    public String formulateQuery(String randomText, String bookTitle, String authorName,
                                 String ISBN) {
        StringBuilder bldr = new StringBuilder();

        if (randomText != null && randomText.length() != 0) {
            String[] queryTokens = randomText.split(" ");
            bldr.append(queryTokens[0]);
            for (int i = 1; i < queryTokens.length; i++) {
                bldr.append('+');
                bldr.append(queryTokens[i]);
            }
        }
        if (bookTitle != null && bookTitle.length() != 0) {
            if (bldr.length() != 0) {
                bldr.append(',');
            }
            bldr.append("intitle:");
            String[] titleTokens = bookTitle.split(" ");
            bldr.append(titleTokens[0]);
            for (int i = 1; i < titleTokens.length; i++) {
                bldr.append('+');
                bldr.append(titleTokens[i]);
            }
        }
        if (authorName != null && authorName.length() != 0) {
            if (bldr.length() != 0) {
                bldr.append(',');
            }
            bldr.append("inauthor:");
            String[] authorTokens = authorName.split(" ");
            bldr.append(authorTokens[0]);
            for (int i = 1; i < authorTokens.length; i++) {
                bldr.append('+');
                bldr.append(authorTokens[i]);
            }
        }
        if (ISBN != null && ISBN.length() != 0) {
            if (bldr.length() != 0) {
                bldr.append(',');
            }
            bldr.append("isbn:");
            bldr.append(ISBN);
        }
        return bldr.toString();
    }

    public static void main(String[] args) {
        try {
            getHandler().queryGoogleBooks(""/* Random Text */
                    , ""/* Title */, "george"/* Author */, ""/* ISBN */);
            return;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}