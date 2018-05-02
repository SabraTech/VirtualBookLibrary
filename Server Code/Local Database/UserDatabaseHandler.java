import com.example.space.virtualbooklibrary.User;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

// singleton class to handle communications with the database.
public class UserDatabaseHandler {

    // singleton instance.
    private static UserDatabaseHandler handler;
    // connection instance.
    private static Connection connection;

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
        if (user.getId().length() < 7 || !isAlphabetic(user.getId().charAt(0))) {
            return new AuthenticationResult(
                    "Invalid ID, ID should be greater than 6 characters starting with an alphabetic char");
        }

        if (user.getPass().length() < 7 || !isAlphabetic(user.getPass().charAt(0)) || !containsNumericDigit(user.getPass())) {
            return new AuthenticationResult(
                    "Invalid Password, password should be greater than 6 characters starting with an alphabetic char with at least 1 numeric digit");
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "insert into Library.User value(" + "\"" + user.getId() + "\", \"" + user
                    .getPass() + "\", \"" + user.getFirstName() + "\", \"" + user.getLastName() + "\", \""
                    + user.getEmail() + "\");";
            int rs = stmt.executeUpdate(query);
            if (rs != 1) {
                return new AuthenticationResult("Error!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AuthenticationResult();
    }

    // Returns the results of the sign in request as an instance of Authentication
    // results.
    public AuthenticationResult signIn(String id, String pass) {
        try {
            Statement stmt = connection.createStatement();
            String query = "Select * from Library.User where id = \"" + id + "\";";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                if (rs.getString(2).equals(pass))
                    return new AuthenticationResult();
                else
                    return new AuthenticationResult("Invalid ID or Password");

            }
            return new AuthenticationResult("Invalid ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AuthenticationResult("Error!");

    }

    // function used internally to check if the user exists in the database.
    private boolean userExists(String username) {
        boolean exists = false;
        try {
            Statement stmt;
            stmt = connection.createStatement();
            String query = "select * from Library.User where User.id = \"" + username + "\"";
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Library"// database
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

    // public static void main(String args[]) {
    // User first = new User("ahmeddd", "password7", "asdas", "asdsad",
    // "asdasfds");
    // User second = new User("Omar2475", "pass", "asdas", "asdsad", "asdasfds");
    // User third = new User("hassan600", "password7", "asdas", "asdsad",
    // "asdasfds");
    // UserDatabaseHandler handler = UserDatabaseHandler.getHandler();
    // System.out.println(handler.signUp(first));
    // System.out.println("---------------------------------------------------------------");
    //
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
    //
    // }

}
