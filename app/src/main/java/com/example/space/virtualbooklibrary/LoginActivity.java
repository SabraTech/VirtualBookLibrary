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

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private String usernameString, passwordString;
    private String URL, errorMesg;
    private ProgressDialog loading;
    private int ans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_password);

        loading = new ProgressDialog(this);

        ans = -1;
        errorMesg = "";
    }

    public void login(View view) {
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();

        if (validate(usernameString, passwordString)) {
            // give the user the session to the app
            URL = "http://192.168.0.104:8080/signin?id="
                    + usernameString + "&password=" + passwordString;

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
                startActivity(new Intent(LoginActivity.this, SearchActivity.class));
                finish();
            } else if (ans == 2) {
                Toast.makeText(this, "Invalid username or password format", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error in connection", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "Invalid username or empty fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    private boolean validate(String usernameStr, String passwordStr) {
        return passwordStr.length() > 0 && usernameStr.length() > 0;
    }

    private void signin() {
        // call the api here and set the private list books.
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(URL);
        try {
            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 202) {
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

    private class Connection extends AsyncTask {

        @Override
        protected Object doInBackground(Object... arg0) {
            signin();
            return null;
        }

    }
}
