package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.api.client.util.Base64;
import com.squareup.picasso.Picasso;
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
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    FloatingActionButton fabSearch, fabFilter;
    RecyclerView recyclerViewBooks;
    ListAllBooksAdapter booksAdapter;
    BookData bookData;
    Picasso picasso;
    private List<Book> books;
    MyFabFragment dialogFrag;
    private String URL, serverIp;
    private ArrayMap<String, List<String>> appliedFilters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        books = new ArrayList<>();
        appliedFilters = new ArrayMap<>();

        Intent intentData = getIntent();

        serverIp = intentData.getStringExtra("server");

        getBooksHistory();

    }

    private void afterBooksQuery() {
        fabSearch = findViewById(R.id.fab_search);
        fabFilter = findViewById(R.id.fab_filter);
        recyclerViewBooks = findViewById(R.id.recycler_books);

        bookData = new BookData(books);
        picasso = Picasso.with(this);

        booksAdapter = new ListAllBooksAdapter(this, books, picasso);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooks.setLayoutManager(linearLayoutManager);
        recyclerViewBooks.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBooks.setAdapter(booksAdapter);

        dialogFrag = MyFabFragment.newInstance();
        dialogFrag.setParentFab(fabFilter);
        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtra("server", serverIp);
                startActivity(intent);
            }
        });
    }

    private void getBooksHistory() {
        // create link for the book history and populate books list
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }
    }

    @Override
    public void onResult(Object result) {
        if (result.toString().equalsIgnoreCase("swiped_down")) {
            // nothing
        } else {
            if (result != null) {
                ArrayMap<String, List<String>> filters = (ArrayMap<String, List<String>>) result;
                if (filters.size() != 0) {
                    List<Book> filteredList = bookData.getAllBooks();
                    for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                        switch (entry.getKey()) {
                            case "rating":
                                filteredList = bookData.getRatingFilteredBooks(entry.getValue(), filteredList);
                                break;
//                            case "year":
//                                filteredList = bookData.getYearFilteredBooks(entry.getValue(), filteredList);
//                                break;
                            case "category":
                                filteredList = bookData.getCategoryFilteredBooks(entry.getValue(), filteredList);
                                break;

                            // add the filter wanted here
                        }
                    }
                    books.clear();
                    books.addAll(filteredList);
                    booksAdapter.notifyDataSetChanged();
                } else {
                    books.addAll(bookData.getAllBooks());
                    booksAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public ArrayMap<String, List<String>> getAppliedFilters() {
        return appliedFilters;
    }

    public BookData getBookData() {
        return bookData;
    }

    @Override
    public void onOpenAnimationStart() {
        Log.d("aah_animation", "onOpenAnimationStart: ");
    }

    @Override
    public void onOpenAnimationEnd() {
        Log.d("aah_animation", "onOpenAnimationEnd: ");
    }

    @Override
    public void onCloseAnimationStart() {
        Log.d("aah_animation", "onCloseAnimationStart: ");
    }

    @Override
    public void onCloseAnimationEnd() {
        Log.d("aah_animation", "onCloseAnimationEnd: ");
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
