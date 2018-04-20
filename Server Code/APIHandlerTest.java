import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
  void TestAllParametersGiven() {
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

}
