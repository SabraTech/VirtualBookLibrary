package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.space.virtualbooklibrary.model.Book;
import com.example.space.virtualbooklibrary.ui.adapters.ListAllBooksAdapter;

import java.util.List;


public class ResultViewActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private ListAllBooksAdapter booksRecyclerAdapter;
    private List<Book> books;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);

        Intent intentData = getIntent();

        String title = intentData.getStringExtra("title");
        String isbn = intentData.getStringExtra("isbn");
        String author = intentData.getStringExtra("author");
        String randomText = intentData.getStringExtra("random");

        getBooksList(title, isbn, author, randomText);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewBooks = findViewById(R.id.recycleBooksList);
        recyclerViewBooks.setLayoutManager(linearLayoutManager);
        booksRecyclerAdapter = new ListAllBooksAdapter(this, books);
        recyclerViewBooks.setAdapter(booksRecyclerAdapter);

    }

    private void getBooksList(String title, String isbn, String author, String randomText) {
        // call the api here and set the private list books.
    }


}
