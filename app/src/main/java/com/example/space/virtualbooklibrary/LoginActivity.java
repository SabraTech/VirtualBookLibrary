package com.example.space.virtualbooklibrary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private String usernameString, passwordString;
    private String URL, errorMesg, serverIp;
    private int ans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.edit_text_username);
        password = findViewById(R.id.edit_text_password);

        ans = -1;
        errorMesg = "";

        getServerIp();
    }

    public void getServerIp() {
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_server_ip, null);
        final AutoCompleteTextView input = view.findViewById(R.id.server_ip);
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("Server IP")
                .setView(R.layout.dialog_server_ip)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        serverIp = input.getText().toString().trim();
                        if (serverIp.length() > 0) {
                            dialogInterface.dismiss();
                        } else {
                            input.setError("Enter the IP!");
                        }
                    }
                }).show();
    }

    public void login(View view) {
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();

        if (validate(usernameString, passwordString)) {
            // give the user the session to the app
            URL = "http://" + serverIp + ":8080/signin?id="
                    + usernameString + "&password=" + passwordString;

            new Connection().execute();

        } else {
            Toast.makeText(this, "Invalid username or empty fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra("server", serverIp);
        startActivity(intent);
    }

    private boolean validate(String usernameStr, String passwordStr) {
        return passwordStr.length() > 0 && usernameStr.length() > 0;
    }

    private void afterSignin() {
        if (ans == 1) {
            // go to search
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("server", serverIp);
            startActivity(intent);
            finish();
        } else if (ans == 2) {
            Toast.makeText(this, errorMesg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error in connection", Toast.LENGTH_SHORT).show();
        }
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

        private LovelyProgressDialog loading = new LovelyProgressDialog(LoginActivity.this).setCancelable(false);

        @Override
        protected Object doInBackground(Object... arg0) {
            signin();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setTitle("Signing...")
                    .setTopColor(getResources().getColor(R.color.colorAccent))
                    .show();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            loading.dismiss();
            afterSignin();
        }
    }
}
