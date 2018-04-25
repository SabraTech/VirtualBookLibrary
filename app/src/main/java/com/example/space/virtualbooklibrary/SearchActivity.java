package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    private EditText title, isbn, author, random;
    private String titleString, isbnString, authorString, randomString;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        title = findViewById(R.id.edit_text_title);
        isbn = findViewById(R.id.edit_text_isbn);
        author = findViewById(R.id.edit_text_author);
        random = findViewById(R.id.edit_text_random);
    }

    public void search(View view) {
        titleString = title.getText().toString();
        isbnString = isbn.getText().toString();
        authorString = author.getText().toString();
        randomString = random.getText().toString();

        if (validate(titleString, isbnString, authorString, randomString)) {
            Intent intent = new Intent(SearchActivity.this, ResultViewActivity.class);
            intent.putExtra("title", titleString);
            intent.putExtra("isbn", isbnString);
            intent.putExtra("author", authorString);
            intent.putExtra("random", randomString);
            startActivity(intent);
        } else {
            Toast.makeText(this, "At least search by one item!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String titleStr, String isbnStr, String authorStr, String randomStr) {
        return (titleStr.length() > 0 || isbnStr.length() > 0 || authorStr.length() > 0 || randomStr.length() > 0);
    }
}
