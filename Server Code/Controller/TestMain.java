import com.google.api.client.util.Base64;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import java.util.List;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import com.example.space.virtualbooklibrary.Book;

public class TestMain {
    public static void main(String[] args) throws Exception {
        HttpClient client = new DefaultHttpClient();
        String URL = "http://localhost:8080/user/gewilli/search?title=game";
        HttpPost request = new HttpPost(URL);
        request.setEntity(new StringEntity("1234"));
        HttpResponse response = client.execute(request);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        response.getEntity().writeTo(output);

        byte[] byteArray = Base64.decodeBase64(output.toByteArray());
        ByteInputStream in = new ByteInputStream(byteArray, byteArray.length);

        List<Book> rtrn = null;
        try {
            rtrn = (List<Book>) new ObjectInputStream(in).readObject();
        } catch (Exception e) {
            System.out.println("FAILURE: " + e.getMessage());
        }
        System.out.println("Number of books returned: " + rtrn.size());
    }
}