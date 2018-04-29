package com.example.space.virtualbooklibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private EditText firstName, lastName, username, email, password, passwordRept;
    private String fnameString, lnameString, usernameString, emailString, passwordString, passwordReptString;
    private String URL, errorMesg;
    private ProgressDialog loading;
    private int ans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstName = findViewById(R.id.edit_text_fname);
        lastName = findViewById(R.id.edit_text_lname);
        username = findViewById(R.id.edit_text_newusername);
        email = findViewById(R.id.edit_text_newemail);
        password = findViewById(R.id.edit_text_newpassword);
        passwordRept = findViewById(R.id.edit_text_newpassword2);

        loading = new ProgressDialog(this);

        ans = -1;
        errorMesg = "";

    }


    public void submit(View view) {
        fnameString = firstName.getText().toString();
        lnameString = lastName.getText().toString();
        usernameString = username.getText().toString();
        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        passwordReptString = passwordRept.getText().toString();

        if (validate(fnameString, lnameString, usernameString, emailString, passwordString, passwordReptString)) {
            // here add the user to the database
            URL = "http://192.168.0.104:8080/signup?id="
                    + usernameString + "&password=" + passwordString + "&firstname=" + fnameString + "&lastname=" + lnameString + "&email=" + emailString;

            loading.setTitle("Loading");
            loading.setCancelable(false);
            loading.setMessage("Registering...");
            loading.show();

            new Connection().execute();

            loading.dismiss();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ans == 1) {
                // go to search
                startActivity(new Intent(RegisterActivity.this, SearchActivity.class));
                finish();
            } else {
                Toast.makeText(this, errorMesg, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void signup() {
        // call the api here and set the private list books.
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(URL);
        try {
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 201) {
                // accept
                this.ans = 1;
            } else {
                // worng id or pass format
                this.ans = 2;
                this.errorMesg = response.getEntity().getContent().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validate(String fname, String lname, String usernameStr, String emailStr, String passStr, String pass2Str) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return usernameStr.length() > 0 && passStr.length() > 6 && pass2Str.equals(passStr) && matcher.find() && fname.length() > 0 && lname.length() > 0;
    }

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            signup();
            return null;
        }

    }
}
