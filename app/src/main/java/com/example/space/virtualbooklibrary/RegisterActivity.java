package com.example.space.virtualbooklibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText firstName, lastName, email, password, passwordRept;
    private String fnameString, lnameString, emailString, passwordString, passwordReptString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.edit_text_fname);
        lastName = findViewById(R.id.edit_text_lname);
        email = findViewById(R.id.edit_text_newemail);
        password = findViewById(R.id.edit_text_newpassword);
        passwordRept = findViewById(R.id.edit_text_newpassword2);

    }


    public void submit(View view) {
        fnameString = firstName.getText().toString();
        lnameString = lastName.getText().toString();
        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        passwordReptString = passwordRept.getText().toString();

        if (validate(fnameString, lnameString, emailString, passwordString, passwordReptString)) {
            // here add the user to the database
        } else {
            Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String fname, String lname, String emailStr, String passStr, String pass2Str) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return passStr.length() > 6 && pass2Str.equals(passStr) && matcher.find() && fname.length() > 0 && lname.length() > 0;
    }
}
