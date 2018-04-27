import static spark.Spark.get;
import static spark.Spark.port;

import com.google.api.client.util.Base64;

import spark.Route;
import spark.Request;
import spark.Response;

import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

import java.util.List;

public class Controller {
    public static void main(String[] args) {
        port(8080);

        get("/books", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String ISBN = request.queryParams("ISBN");
                String bookTitle = request.queryParams("title");
                String randomText = request.queryParams("random");
                String authorName = request.queryParams("author");

                List<Book> results = null;
                ByteArrayOutputStream output = null;

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
    }
}
