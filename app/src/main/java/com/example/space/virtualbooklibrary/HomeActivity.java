package com.example.space.virtualbooklibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.google.api.client.util.Base64;
import com.squareup.picasso.Picasso;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks, AAH_FabulousFragment.AnimationListener {

    FloatingActionButton fabSearch, fabFilter;
    RecyclerView recyclerViewBooks;
    BooksAdapter booksAdapter;
    BookData bookData;
    Picasso picasso;
    private List<Book> books, favouriteBooks;
    MyFabFragment dialogFrag;
    private String homeURL, searchURL, favouriteURL, serverIp, username, sessionToken;
    private ArrayMap<String, List<String>> appliedFilters;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        books = new ArrayList<>();
        favouriteBooks = new ArrayList<>();
        appliedFilters = new ArrayMap<>();

        Intent intentData = getIntent();

        serverIp = intentData.getStringExtra("server");
        username = intentData.getStringExtra("username");
        sessionToken = intentData.getStringExtra("token");

        getBooksHistory();

    }

    private void afterHistoryQuery() {
        fabSearch = findViewById(R.id.fab_search);
        fabFilter = findViewById(R.id.fab_filter);
        recyclerViewBooks = findViewById(R.id.recycler_books);

        Set<String> bookIdSet = new HashSet<>();
        for (Book book : favouriteBooks) {
            bookIdSet.add(book.getId());
        }

        books.addAll(favouriteBooks);
        bookData = new BookData(books);
        picasso = Picasso.with(this);

        booksAdapter = new BooksAdapter(this, books, bookIdSet, picasso, serverIp, sessionToken, username);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooks.setLayoutManager(linearLayoutManager);
        recyclerViewBooks.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBooks.setAdapter(booksAdapter);

        dialogFrag = MyFabFragment.newInstance();
        dialogFrag.setParentFab(fabFilter);
    }

    public void filter(View view) {
        dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
    }

    public void search(View view) {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search, null);
        final EditText title = dialogView.findViewById(R.id.edit_text_title);
        final EditText isbn = dialogView.findViewById(R.id.edit_text_isbn);
        final EditText author = dialogView.findViewById(R.id.edit_text_author);
        final EditText random = dialogView.findViewById(R.id.edit_text_random);
        new AlertDialog.Builder(this)
                .setTitle("Find Books")
                .setView(dialogView)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String titleString = title.getText().toString();
                        String isbnString = isbn.getText().toString();
                        String authorString = author.getText().toString();
                        String randomString = random.getText().toString();

                        if (validate(titleString, isbnString, authorString, randomString)) {
                            searchURL = "http://" + serverIp + ":8080/user/" + username +
                                    "/search?ISBN=" + isbnString + "&author=" + authorString + "&title=" + titleString + "&random=" + randomString;
                            dialogInterface.dismiss();
                            new SearchQuery().execute();
                        } else {
                            Toast.makeText(HomeActivity.this, "At least search by one item!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void afterSearchQuery() {
        bookData.setBookList(books);
        booksAdapter.notifyDataSetChanged();
    }

    private boolean validate(String titleStr, String isbnStr, String authorStr, String randomStr) {
        return (titleStr.length() > 0 || isbnStr.length() > 0 || authorStr.length() > 0 || randomStr.length() > 0);
    }

    private void getBooksHistory() {
        // create link for the book history and populate books list
        homeURL = "http://" + serverIp + ":8080/home/" + username;
        favouriteURL = "http://" + serverIp + ":8080/user/" + username + "/favourites";

        new HomeQuery().execute();
    }

    private void getHomeBooksList() {
        // call the api here and set the private list books.
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(homeURL);
        HttpPost request2 = new HttpPost(favouriteURL);
        try {
            request.setEntity(new StringEntity(this.sessionToken));
            request2.setEntity(new StringEntity(this.sessionToken));

            HttpResponse response = client.execute(request);
            HttpResponse response2 = client.execute(request2);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream output2 = new ByteArrayOutputStream();

            response.getEntity().writeTo(output);
            response2.getEntity().writeTo(output2);

            byte[] byteArray = Base64.decodeBase64(output.toByteArray());
            byte[] byteArray2 = Base64.decodeBase64(output2.toByteArray());

            ByteInputStream input = new ByteInputStream(byteArray, byteArray.length);
            ByteInputStream input2 = new ByteInputStream(byteArray2, byteArray2.length);


            List<Book> rtrn1 = new ArrayList<>();
            List<Book> rtrn2 = new ArrayList<>();

            Object object = new ObjectInputStream(input).readObject();
            Object object2 = new ObjectInputStream(input2).readObject();

            rtrn1 = (List<Book>) object;
            this.books.clear();
            this.books.addAll(rtrn1);

            rtrn2 = (List<Book>) object2;
            this.favouriteBooks.clear();
            this.favouriteBooks.addAll(rtrn2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSearchBooksList() {
        // call the api here and set the private list books.
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(searchURL);
        try {
            request.setEntity(new StringEntity(this.sessionToken));
            HttpResponse response = client.execute(request);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.getEntity().writeTo(output);

            byte[] byteArray = Base64.decodeBase64(output.toByteArray());
            ByteInputStream input = new ByteInputStream(byteArray, byteArray.length);

            List<Book> rtrn = new ArrayList<>();
            Object object = new ObjectInputStream(input).readObject();
            rtrn = (List<Book>) object;
            this.books.clear();
            this.books.addAll(rtrn);
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

    private class HomeQuery extends AsyncTask {

        private LovelyProgressDialog loading = new LovelyProgressDialog(HomeActivity.this).setCancelable(false);


        @Override
        protected Object doInBackground(Object[] objects) {
            getHomeBooksList();
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
            afterHistoryQuery();
        }
    }

    private class SearchQuery extends AsyncTask {

        private LovelyProgressDialog loading = new LovelyProgressDialog(HomeActivity.this).setCancelable(false);


        @Override
        protected Object doInBackground(Object[] objects) {
            getSearchBooksList();
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
            afterSearchQuery();
        }
    }
}
