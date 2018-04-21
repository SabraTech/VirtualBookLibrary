package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.api.client.util.Base64;
import com.sun.xml.messaging.saaj.util.ByteInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;


public class ResultViewActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private ListAllBooksAdapter booksRecyclerAdapter;
    private List<Book> books;
    private String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);

        Intent intentData = getIntent();

        String title = intentData.getStringExtra("title");
        String isbn = intentData.getStringExtra("isbn");
        String author = intentData.getStringExtra("author");
        String randomText = intentData.getStringExtra("random");

        title = title.replaceAll(" ", "+");
        author = author.replaceAll(" ", "+");
        randomText = randomText.replaceAll(" ", "+");
        URL = "http://192.168.43.134:8080/books?ISBN="
                + isbn + "&author=" + author + "&title=" + title + "&random=" + randomText;

        new Connection().execute();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooks = findViewById(R.id.recycleBooksList);
        recyclerViewBooks.setLayoutManager(linearLayoutManager);
        booksRecyclerAdapter = new ListAllBooksAdapter(this, books);
        recyclerViewBooks.setAdapter(booksRecyclerAdapter);

    }

    private void getBooksList() {
        // call the api here and set the private list books.
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(request);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.getEntity().writeTo(output);

            byte[] byteArray = Base64.decodeBase64(output.toByteArray());
            ByteInputStream input = new ByteInputStream(byteArray, byteArray.length);

            List<Book> rtrn = new ArrayList<>();
            Object object = new ObjectInputStream(input).readObject();
            rtrn = (List<Book>) object;
            this.books = rtrn;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            getBooksList();
            return null;
        }

    }
}
