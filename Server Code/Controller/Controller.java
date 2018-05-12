import static spark.Spark.port;
import static spark.Spark.post;

import com.google.api.client.util.Base64;

import com.example.space.virtualbooklibrary.Book;
import com.example.space.virtualbooklibrary.User;

import spark.Route;
import spark.Request;
import spark.Response;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

import java.util.UUID;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class Controller {
    public static void main(String[] args) {
        port(8080);
        final HashMap<String, String> users = new HashMap<>();

        post("/home/:id", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String user = request.params(":id");
                String userUUID = users.get(user);

                if (request.body().equals(userUUID)) {
                    try {
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(output);
                        out.writeObject(UserDatabaseHandler.getHandler().getHistory(user));
                        out.flush();

                        response.status(200);
                        return Base64.encodeBase64String(output.toByteArray());
                    } catch (Exception e) {
                        System.out.println("FAILURE: " + e.getMessage());
                    }
                }
                response.status(406);
                return "Failure";
            }
        });

        post("/user/:id/favourites", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String user = request.params(":id");
                String userUUID = users.get(user);

                if (request.body().equals(userUUID)) {
                    try {
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(output);
                        out.writeObject(UserDatabaseHandler.getHandler().getFavourites(user));
                        out.flush();

                        response.status(200);
                        return Base64.encodeBase64String(output.toByteArray());
                    } catch (Exception e) {
                        System.out.println("FAILURE: " + e.getMessage());
                    }
                }
                response.status(406);
                return "Failure";
            }
        });

        post("/user/:id/favourite", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String user = request.params(":id");
                String userUUID = users.get(user);

                String ISBN = "";
                ISBN = request.queryParams("ISBN");
                String bookTitle = "";
                bookTitle = request.queryParams("title");

                if (request.body().equals(userUUID)) {
                    if (UserDatabaseHandler.getHandler().addFavourite(user, ISBN, bookTitle).isValid()) {
                        response.status(200);
                        return "Success";
                    }
                }
                response.status(406);
                return "Failure";
            }
        });

        post("/user/:id/search", new Route() {
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

                ByteArrayOutputStream output = null;
                String user = request.params(":id");
                String userUUID = users.get(user);
                List<Book> results = new ArrayList<>();

                if (request.body().equals(userUUID)) {
                    System.out.println("Searching..");

                    try {
                        for (int i = 0; i < 5; i++) {
                            results.addAll(APIHandler.getHandler().queryGoogleBooks(
                                    randomText, bookTitle, authorName, ISBN, i, user, false));
                        }
                        output = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(output);
                        out.writeObject(results);
                        out.flush();
                    } catch (Exception e) {
                        System.out.println("FAILURE: " + e.getMessage());
                    }

                    response.status(200);

                    return Base64.encodeBase64String(output.toByteArray());
                } else {
                    System.out.println("Wrong UUID");
                    return "Wrong UUID";
                }
            }
        });

        post("/signin", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String ID = "";
                ID = request.queryParams("id");
                String password = "";
                password = request.body();

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
                String ID = "";
                ID = request.queryParams("id");
                String password = "";
                password = request.body();
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