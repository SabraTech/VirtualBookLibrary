package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.api.client.util.Base64;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private ListAllBooksAdapter booksAdapter;
    private List<Book> books;
    private String URL, serverIp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intentData = getIntent();

        serverIp = intentData.getStringExtra("server");

        getBooksHistory();

    }

    private void afterBooksQuery() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooks = findViewById(R.id.recycler_books);
        recyclerViewBooks.setLayoutManager(linearLayoutManager);
        booksAdapter = new ListAllBooksAdapter(this, books);
        recyclerViewBooks.setAdapter(booksAdapter);
    }

    private void getBooksHistory() {

        URL = URL = "http://" + serverIp + ":8080/filter?ISBN=";

        new Connection().execute();
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


    public void filter(View view) {
        // input dialog


        // call the connection with URL2 to get books and view them
    }

    public void openSearch(View view) {
        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
        intent.putExtra("server", serverIp);
        startActivity(intent);
    }

    private class Connection extends AsyncTask {

        private LovelyProgressDialog loading = new LovelyProgressDialog(HomeActivity.this).setCancelable(false);


        @Override
        protected Object doInBackground(Object[] objects) {
            getBooksList();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setTitle("Loading Books...")
                    .setTopColor(getResources().getColor(R.color.colorAccent))
                    .show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            loading.dismiss();
            afterBooksQuery();
        }
    }
}
