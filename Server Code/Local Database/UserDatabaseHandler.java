import com.example.space.virtualbooklibrary.Book;
import com.example.space.virtualbooklibrary.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// singleton class to handle communications with the database.
public class UserDatabaseHandler {

    // singleton instance.
    private static UserDatabaseHandler handler;
    // connection instance.
    private static Connection con;

    // getter for the singleton instance.
    public static UserDatabaseHandler getHandler() {
        if (handler == null)
            handler = new UserDatabaseHandler();
        return handler;
    }

    // signs up the given user in case of valid id and pass, returns the result as
    // an instance of AuthenticationResults.
    // Id should be >= 7 chars starting with an alphabetic char.
    // Pass should be >= 7 chars starting with an alphabetic char and containg at
    // least 1 numeric char.
    public AuthenticationResult signUp(User user) {
        if (userExists(user.getId())) {
            return new AuthenticationResult("ID already exists!");
        }
        if (user.getId().length() < 7 || !isAlphabetic(user.getId().charAt(0)))
            return new AuthenticationResult(
                    "Invalid ID, ID should be greater than 6 characters starting with an alphabetic char");

        if (user.getPass().length() < 7 || !isAlphabetic(user.getPass().charAt(0))
                || !containsNumericDigit(user.getPass()))
            return new AuthenticationResult(
                    "Invalid Pass, password should be greater than 6 characters starting with an alphabetic char with at least 1 numeric digit");
        try {
            Statement stmt = con.createStatement();
            String query = "insert into Library.User value(" + "\"" + user.getId() + "\", \"" + user
                    .getPass() + "\", \"" + user.getFirstName() + "\", \"" + user.getLastName() + "\", \""
                    + user.getEmail() + "\");";
            System.out.println(query);
            int rs = stmt.executeUpdate(query);
            if (rs != 1)
                return new AuthenticationResult("Error!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AuthenticationResult();
    }

    // Returns the results of the sign in request as an instance of Authentication
    // results.
    public AuthenticationResult signIn(String id, String pass) {
        try {
            Statement stmt = con.createStatement();
            String query = "Select * from Library.User where id = \"" + id + "\";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                if (rs.getString(2).equals(pass))
                    return new AuthenticationResult();
                else
                    return new AuthenticationResult("Invalid id or pass");

            }
            return new AuthenticationResult("Invalid id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AuthenticationResult("Error!");

    }

    public AuthenticationResult deleteUser(String userName) {
        if (!userExists(userName)) {
            return new AuthenticationResult("User not registered!");
        }
        try {
            Statement stmt = con.createStatement();
            String query = "delete from Library.User where User.id = " + "\"" + userName + "\";";
            System.out.println(query);
            int rs = stmt.executeUpdate(query);
            if (rs != 1)
                return new AuthenticationResult("Error!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Success!
        return new AuthenticationResult();
    }

    public AuthenticationResult addFavourite(String userName, String bookID, String bookTitle) {
        if (!userExists(userName)) {
            return new AuthenticationResult("Invalid User");
        }
        if (bookID == null || bookID.isEmpty())
            return new AuthenticationResult("Invalid Book ID: " + bookID);
        if (favouriteExists(userName, bookID))
            return new AuthenticationResult();
        try {
            Statement stmt = con.createStatement();
            String query = "insert into Library.Favourites value(" + "\"" + userName + "\", \"" + bookID
                    + "\", \"" + bookTitle + "\");";
            System.out.println(query);
            int rs = stmt.executeUpdate(query);
            if (rs != 1)
                return new AuthenticationResult("Error!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new AuthenticationResult();
    }

    public AuthenticationResult addHistory(String userName, String bookID, String bookTitle) {
        if (!userExists(userName)) {
            return new AuthenticationResult("Invalid User");
        }
        if (bookID == null || bookID.isEmpty())
            return new AuthenticationResult("Invalid ISBN: " + bookID);
        if (historyExists(userName, bookID))
            return new AuthenticationResult();
        try {
            Statement stmt = con.createStatement();
            String query = "insert into Library.History value(" + "\"" + userName + "\", \"" + bookID
                    + "\", \"" + bookTitle + "\");";
            System.out.println(query);
            int rs = stmt.executeUpdate(query);
            if (rs != 1)
                return new AuthenticationResult("Error!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new AuthenticationResult();
    }

    // Get the books that the user marked as favorite from the database.
    // returns a maximum of 10 results.
    public ArrayList<Book> getFavourites(String userName) {
        ArrayList<Book> favourites = new ArrayList<Book>();
        ArrayList<Pair> bookIDsAndTitles = new ArrayList<Pair>();
        try {
            Statement stmt = con.createStatement();
            String query = "Select * from Library.Favourites where Favourites.User_ID = \"" + userName
                    + "\"" + "order by Book_Title limit 0,10" + ";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                bookIDsAndTitles.add(new Pair(rs.getString(2), rs.getString(3)));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(bookIDsAndTitles);
        try {
            favourites = APIHandler.getHandler().getBooksWithIDs(bookIDsAndTitles);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return favourites;
    }

    // Get the books that appeared in user searches before.
    // returns a maximum of 10 results.
    public ArrayList<Book> getHistory(String userName) {
        ArrayList<Book> history = new ArrayList<Book>();
        ArrayList<Pair> booksIDsAndTitles = new ArrayList<Pair>();
        try {
            Statement stmt = con.createStatement();
            String query = "Select * from Library.History where History.User_ID = \"" + userName + "\""
                    + "order by Book_Title limit 0,10" + ";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                booksIDsAndTitles.add(new Pair(rs.getString(2), rs.getString(3)));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(booksIDsAndTitles);
        try {
            history = APIHandler.getHandler().getBooksWithIDs(booksIDsAndTitles);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return history;
    }

    // function used internally to check if the user exists in the database.
    private boolean userExists(String userName) {
        boolean exists = false;
        try {
            Statement stmt;
            stmt = con.createStatement();
            String query = "select * from Library.User where User.id = \"" + userName + "\"";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            exists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    // private constructor.
    private UserDatabaseHandler() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library"// database
                    , "root"// username
                    , ""// password
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // function used internally, returns true if c is an alphabetic char.
    private boolean isAlphabetic(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    // function used internally to check if the string contains a numeric char.
    private boolean containsNumericDigit(String s) {
        for (char c : s.toCharArray()) {
            if (c >= '0' && c < '9')
                return true;
        }
        return false;
    }

    // function used internally to check if the user has previously marked this
    // book as favorite.
    private boolean favouriteExists(String userName, String ISBN) {
        boolean exists = false;
        try {
            Statement stmt;
            stmt = con.createStatement();
            String query = "select * from Library.Favourites where Favourites.User_ID = \"" + userName
                    + "\"" + " and Favourites.Book_ID = \"" + ISBN + "\";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    // function used internally to check if this book was previously added to user
    // history.
    private boolean historyExists(String userName, String ISBN) {
        boolean exists = false;
        try {
            Statement stmt;
            stmt = con.createStatement();
            String query = "select * from Library.History where History.User_ID = \"" + userName + "\""
                    + " and History.Book_ID = \"" + ISBN + "\";";
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exists;
    }

    public static void main(String args[]) throws Exception {
        User first = new User("matefff", "password7", "asdas", "asdsad", "asdasfds");
        // User second = new User("Omar2475", "pass", "asdas", "asdsad",
        // "asdasfds");
        // User third = new User("hassan600", "password7", "asdas", "asdsad",
        // "asdasfds");
        UserDatabaseHandler handler = UserDatabaseHandler.getHandler();
        System.out.println(handler.signUp(first));
        System.out.println("---------------------------------------------------------------");

        // System.out.println(handler.signIn(first.getId(), first.getPass()));
        // System.out.println("---------------------------------------------------------------");
        //
        // System.out.println(handler.signIn(second.getId(), second.getPass()));
        // System.out.println("---------------------------------------------------------------");
        //
        // System.out.println(handler.signUp(first));
        // System.out.println("---------------------------------------------------------------");
        //
        // System.out.println(handler.signUp(second));
        // System.out.println("---------------------------------------------------------------");
        // System.out.println(handler.signUp(third));
        System.out.println("---------------------------------------------------------------");
        // System.out.println(handler.deleteUser(first.getId()));
        // handler.getFavourites(first.getId());
        APIHandler.getHandler().queryGoogleBooks("", "Game of thrones", "", "", 0, first.getId(),
                false);
        // Pair[] favs = new Pair[] { new Pair("5NomkK4EV68C", "A Game of Thrones"),
        // new Pair(
        // "d1tcIWvinTEC", "Game of Thrones and Philosophy"), new
        // Pair("-ssYBwAAQBAJ",
        // "Game of Thrones on Business") };
        // for (Pair pair : favs)
        // UserDatabaseHandler.getHandler().addFavourite(first.getId(), pair.bookID,
        // pair.bookTitle);
        System.out.println("---------------------------------------------------------------");

        handler.getHistory(first.getId());
        System.out.println("---------------------------------------------------------------");
        System.out.println(handler.deleteUser(first.getId()));

    }

}