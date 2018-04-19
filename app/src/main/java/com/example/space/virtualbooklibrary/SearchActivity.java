package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends AppCompatActivity {

    private EditText title, isbn, author, random;

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
        Intent intent = new Intent(SearchActivity.this, ResultViewActivity.class);
        intent.putExtra("title", title.getText().toString());
        intent.putExtra("isbn", isbn.getText().toString());
        intent.putExtra("author", author.getText().toString());
        intent.putExtra("random", random.getText().toString());
        startActivity(intent);
    }
}
