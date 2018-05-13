import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class UserDatabaseHandlerTest {

    @Test
    void testSignUpValidIDAndPass() {
        String id = "mohamed", pass = "mohamed123";
        UserDatabaseHandler.getHandler().deleteUser(id);
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        result = UserDatabaseHandler.getHandler().deleteUser(id);
    }

    @Test
    void testDeleteUser() {
        String id = "mohamed", pass = "mohamed123";
        UserDatabaseHandler.getHandler().deleteUser(id);
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        result = UserDatabaseHandler.getHandler().deleteUser(id);
        assertTrue(result.isValid());
    }

    @Test
    void testSignUpShortID() {
        User user = new User("moatef", "mohamed123", "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertFalse(result.isValid());
    }

    @Test
    void testSignUpIDNotStartWithAlpha() {
        User user = new User("1moatef", "mohamed123", "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertFalse(result.isValid());
    }

    @Test
    void testSignUpShortPass() {
        User user = new User("mohamed", "mo", "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertFalse(result.isValid());
    }

    @Test
    void testSignUpPassNotStartWithAlpha() {
        User user = new User("mohamed", "1mohamed", "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertFalse(result.isValid());
    }

    @Test
    void testSignUpPassNotContainNumericDigit() {
        User user = new User("mohamed", "1mohamed", "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertFalse(result.isValid());
    }

    @Test
    void testSignInValidCredentials() {
        String id = "mohamed", pass = "mohamed123";
        UserDatabaseHandler.getHandler().deleteUser(id);
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        result = UserDatabaseHandler.getHandler().signIn(id, pass);
        assertTrue(result.isValid());
        UserDatabaseHandler.getHandler().deleteUser(id);
    }

    @Test
    void testSignInInvalidID() {
        AuthenticationResult result = UserDatabaseHandler.getHandler().signIn("testID", "testPass");
        assertFalse(result.isValid());
    }

    @Test
    void testSignInInvalidPass() {
        String id = "mohamed", pass = "mohamed123";
        UserDatabaseHandler.getHandler().deleteUser(id);
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        result = UserDatabaseHandler.getHandler().signIn(id, "wrongpass123");
        assertFalse(result.isValid());
        UserDatabaseHandler.getHandler().deleteUser(id);
    }

    // new tests.

    @Test
    void testAddToFavouritesAndGetFavourites() {
        String id = "mohamed", pass = "mohamed123";
        String bookTitle = "Re-Reading a Game of Thrones", bookID = "l6xMUQ88vLAC";
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        UserDatabaseHandler.getHandler().addFavourite(id, bookID, bookTitle);
        ArrayList<Book> fav = UserDatabaseHandler.getHandler().getFavourites(id);
        assertEquals(bookTitle, fav.get(0).getTitle());
        assertEquals(bookID, fav.get(0).getId());
        UserDatabaseHandler.getHandler().deleteUser(id);
    }

    @Test
    void testAddToHistoryAndGetHistory() {
        String id = "mohamed", pass = "mohamed123";
        String bookTitle = "Re-Reading a Game of Thrones", bookID = "l6xMUQ88vLAC";
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        UserDatabaseHandler.getHandler().addHistory(id, bookID, bookTitle);
        ArrayList<Book> history = UserDatabaseHandler.getHandler().getHistory(id);
        assertEquals(bookTitle, history.get(0).getTitle());
        assertEquals(bookID, history.get(0).getId());
        UserDatabaseHandler.getHandler().deleteUser(id);
    }

    @Test
    void testDeleteUserFavouritesDeleted() {
        String id = "mohamed", pass = "mohamed123";
        String bookTitle = "Re-Reading a Game of Thrones", bookID = "l6xMUQ88vLAC";
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        UserDatabaseHandler.getHandler().addFavourite(id, bookID, bookTitle);
        result = UserDatabaseHandler.getHandler().deleteUser(id);
        assertTrue(result.isValid());
        ArrayList<Book> fav = UserDatabaseHandler.getHandler().getFavourites(id);
        assertTrue(fav.isEmpty());
    }

    @Test
    void testDeleteUserHistoryDeleted() {
        String id = "mohamed", pass = "mohamed123";
        String bookTitle = "Re-Reading a Game of Thrones", bookID = "l6xMUQ88vLAC";
        User user = new User(id, pass, "Name", "lastName", "email@gmail.com");
        AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
        assertTrue(result.isValid());
        UserDatabaseHandler.getHandler().addHistory(id, bookID, bookTitle);
        result = UserDatabaseHandler.getHandler().deleteUser(id);
        assertTrue(result.isValid());
        ArrayList<Book> history = UserDatabaseHandler.getHandler().getHistory(id);
        assertTrue(history.isEmpty());
    }

}