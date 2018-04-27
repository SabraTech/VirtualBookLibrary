package com.example.space.virtualbooklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText email, password;
    private String emailString, passwordString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.edit_text_email);
        password = findViewById(R.id.edit_text_password);

    }

    public void login(View view) {
        emailString = email.getText().toString();
        passwordString = email.getText().toString();

        if (validate(emailString, passwordString)) {
            // give the user the session to the app
        } else {
            Toast.makeText(this, "Invalid email or empty fields", Toast.LENGTH_SHORT).show();
        }

        // send data to database to enter the session
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    private boolean validate(String emailStr, String passwordStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return (passwordStr.length() > 0 || passwordStr.equals(";")) && matcher.find();
    }
}
