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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private String usernameString, passwordString;
    private String URL, errorMesg, serverIp = "192.168.43.134", sessionToken;
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
                        Toast.makeText(LoginActivity.this, "serverIp: " + serverIp, Toast.LENGTH_SHORT).show();

                    }
                }).show();
    }

    public void login(View view) {
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();

        if (validate(usernameString, passwordString)) {
            // give the user the session to the app
            URL = "http://" + serverIp + ":8080/signin?id="
                    + usernameString;

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
            intent.putExtra("username", usernameString);
            intent.putExtra("token", sessionToken);
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
            request.setEntity(new StringEntity(this.passwordString));
            HttpResponse response = client.execute(request);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.getEntity().writeTo(output);
            int code = response.getStatusLine().getStatusCode();
            if (code == 202) {
                // accept
                this.ans = 1;
                this.sessionToken = new String(output.toByteArray());
            } else {
                // worng id or pass format
                this.ans = 2;
                this.errorMesg = new String(output.toByteArray());
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
