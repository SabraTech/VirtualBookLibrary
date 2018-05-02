import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import com.example.space.virtualbooklibrary.Book;
import com.example.space.virtualbooklibrary.User;
import com.google.api.client.util.Base64;

import spark.Route;
import spark.Request;
import spark.Response;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Controller {
    public static void main(String[] args) {
        port(8080);
        final HashMap<String, String> users = new HashMap<>();

        get("/books", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String ISBN = "";
                ISBN = request.queryParams("ISBN");
                String bookTitle = "";
                bookTitle = request.queryParams("title");
                String randomText = "";
                randomText = request.queryParams("random");
                String authorName = "";
                authorName = request.queryParams("author");

                List<Book> results = null;
                ByteArrayOutputStream output = null;

//                String[] tokens = request.body().split("#");
//
//                if (tokens[1].equals(users.get(tokens[0]))) {
//                    System.out.println("Correct");
//                } else {
//                    System.out.println("Wrong UUID");
//                }

                try {
                    results = APIHandler.getHandler()
                            .queryGoogleBooks(randomText, bookTitle, authorName, ISBN);
                    output = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(output);
                    out.writeObject(results);
                    out.flush();
                } catch (Exception e) {
                    System.out.println("FAILURE: " + e.getMessage());
                }

                response.status(200);

                return Base64.encodeBase64String(output.toByteArray());
            }
        });

        post("/signin", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                System.out.println("Sign In");
                String ID = "";
                ID = request.queryParams("id");
                String password = "";
                password = request.queryParams("password");

                AuthenticationResult result = UserDatabaseHandler.getHandler().signIn(ID, password);
                if (result.isValid()) {
                    response.status(202);
                    users.put(ID, UUID.randomUUID().toString());
                    System.out.println(users.get(ID));
                    return users.get(ID);
                } else {
                    response.status(406);
                    return result.error();
                }
            }
        });

        post("/signup", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                System.out.println("Sing Up");
                String ID = "";
                ID = request.queryParams("id");
                String password = "";
                password = request.queryParams("password");
                String firstName = "";
                firstName = request.queryParams("firstname");
                String lastName = "";
                lastName = request.queryParams("lastname");
                String email = "";
                email = request.queryParams("email");

                User user = new User(ID, password, firstName, lastName, email);

                AuthenticationResult result = UserDatabaseHandler.getHandler().signUp(user);
                if (result.isValid()) {
                    response.status(201);
                    users.put(ID, UUID.randomUUID().toString());
                    System.out.println(users.get(ID));
                    return users.get(ID);
                } else {
                    response.status(406);
                    return result.error();
                }
            }
        });
    }
}
