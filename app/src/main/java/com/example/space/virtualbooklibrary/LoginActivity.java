package com.example.space.virtualbooklibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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

        if (TextUtils.isEmpty(usernameString)) {
            username.setError("Enter username!");
            return;
        }

        if (TextUtils.isEmpty(passwordString)) {
            password.setError("Enter password!");
            return;
        }

        // send data to database to enter the session
    }

    public void register(View view) {
        // go to the register activity
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
//        finish();

    }
}
