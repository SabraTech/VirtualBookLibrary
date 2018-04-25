package com.example.space.virtualbooklibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {


    private EditText name, username, password, passwordRept;
    private String nameString, usernameString, passwordString, passwordReptString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.edit_text_name);
        username = findViewById(R.id.edit_text_newusername);
        password = findViewById(R.id.edit_text_newpassword);
        passwordRept = findViewById(R.id.edit_text_newpassword2);

    }


    public void submit(View view) {
        nameString = name.getText().toString();
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();
        passwordReptString = passwordRept.getText().toString();

        if (validate(usernameString, passwordString, passwordReptString)) {
            // here add the user to the database
        } else {
            Toast.makeText(this, "Invalid username or not match password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(String usernameStr, String passStr, String pass2Str) {
        return passStr.length() > 6 && pass2Str.equals(passStr) && usernameStr.length() > 0;
    }
}
