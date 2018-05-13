import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class APIHandlerTest {

    @Test
    void testEmptyQuery() {
        String query = APIHandler.getHandler().formulateQuery("" // random query
                , "" // title
                , "" // author
                , "" // ISBN
        );
        assertEquals("", query);
    }

    @Test
    void testRandomQuery() {
        String query = APIHandler.getHandler().formulateQuery("Jaime Lannister" // random
                // query
                , "" // title
                , "" // author
                , "" // ISBN
        );
        ;
        assertEquals("Jaime+Lannister", query);
    }

    @Test
    void testAuthor() {
        String query = APIHandler.getHandler().formulateQuery("" // random query
                , "" // title
                , "George Martin" // author
                , "" // ISBN
        );
        assertEquals("inauthor:George+Martin", query);
    }

    @Test
    void testAuthorAndTitle() {
        String query = APIHandler.getHandler().formulateQuery("" // random query
                , "Game of Thrones" // title
                , "George Martin" // author
                , "" // ISBN
        );
        assertEquals("intitle:Game+of+Thrones,inauthor:George+Martin", query);
    }

    @Test
    void testAuthorAndISBN() {
        String query = APIHandler.getHandler().formulateQuery("" // random query
                , "" // title
                , "George Martin" // author
                , "A324894345123215fg" // ISBN
        );
        assertEquals("inauthor:George+Martin,isbn:A324894345123215fg", query);
    }

    @Test
    void testAllParametersGiven() {
        String query = APIHandler.getHandler().formulateQuery("Jaime Lannister" // random
                // query

                , "Game of Thrones" // title
                , "George Martin" // author
                , "A324894345123215fg" // ISBN
        );
        assertEquals(
                "Jaime+Lannister,intitle:Game+of+Thrones,inauthor:George+Martin,isbn:A324894345123215fg",
                query);
    }

    // new

    void testQueryAddedToHistory() throws Exception {
        String id = "mohamed", pass = "mohamed123";
        String bookTitle = "game";
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        ArrayList<Book> fav = UserDatabaseHandler.getHandler().getHistory(id);
        assertTrue(fav.isEmpty());
        APIHandler.getHandler().queryGoogleBooks("" // random text.
                , bookTitle // book title.
                , "" // author.
                , "" // ISBN.
                , 0 // page.
                , id // user id.
                , false // get a single result.
        );
        ArrayList<Book> history = UserDatabaseHandler.getHandler().getHistory(id);
        assertFalse(history.isEmpty());
        ArrayList<Book> query = APIHandler.getHandler().queryGoogleBooks("" // random
                // text.
                , bookTitle // book title.
                , "" // author.
                , "" // ISBN.
        );
        assertFalse(query.isEmpty());
        // Comparator<Book> comparator = new Comparator<Book>() {
        // public int compare(Book one, Book two) {
        // return one.getId().compareTo(two.getId());
        // }
        // };
        // Collections.sort(history, comparator);
        // Collections.sort(query, comparator);
        assertEquals(query, history);
        result = UserDatabaseHandler.getHandler().deleteUser(id);
        assertTrue(result.isValid());
    }

}