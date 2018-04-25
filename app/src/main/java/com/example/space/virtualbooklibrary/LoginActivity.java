package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private String usernameString, passwordString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_password);

    }

    public void login(View view) {
        usernameString = username.getText().toString();
        passwordString = username.getText().toString();

        if (validate(usernameString, passwordString)) {
            // give the user the session to the app
        } else {
            Toast.makeText(this, "Invalid username or empty fields", Toast.LENGTH_SHORT).show();
        }

        // send data to database to enter the session
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    private boolean validate(String usernameStr, String passwordStr) {
        return (usernameStr.length() > 0 || passwordStr.length() > 0);
    }
}
