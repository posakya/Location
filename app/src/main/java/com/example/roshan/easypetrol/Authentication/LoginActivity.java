package com.example.roshan.easypetrol.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roshan.easypetrol.DbHandler.User_Profile;
import com.example.roshan.easypetrol.HttpHandler;
import com.example.roshan.easypetrol.MainActivity;
import com.example.roshan.easypetrol.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_email, edit_pass;
    private TextView txt_forget, txt_Sign_up;
    private Button btn_sign_in;
    String email;
    String password;
    String type;

    SharedPreferences prefs;
    private User_Profile userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_pass = (EditText) findViewById(R.id.edit_password);
        btn_sign_in = (Button) findViewById(R.id.btn_login);
        txt_Sign_up = (TextView) findViewById(R.id.txt_sign_up);
        btn_sign_in.setOnClickListener(this);
        txt_Sign_up.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.drawable.appiy);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        userDb = new User_Profile(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        if (v == btn_sign_in) {
            sign_in();
            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
            animation1.setDuration(1000);
            v.startAnimation(animation1);
        }
        if (v == txt_Sign_up) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
    }

    private void sign_in() {
        email = edit_email.getText().toString().trim();
        password = edit_pass.getText().toString().trim();

        if (email.equals("") || password.equals("")) {

            Toast.makeText(LoginActivity.this, "Email or password must be filled", Toast.LENGTH_LONG).show();

            return;

        }
        if (email.length() <= 5 || password.length() <= 5) {

            Toast.makeText(LoginActivity.this, "Email or password length must be greater than five", Toast.LENGTH_LONG).show();

            return;

        }
        new Background(LoginActivity.this).execute("login", email, password);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                invalidateOptionsMenu();
                return super.onOptionsItemSelected(item);
        }
    }


    ////////////////////////////////////////////////////////////////Background/////////////////////

    public class Background extends AsyncTask<String, Void, String> {
        Context context;
        Activity activity;

        public Background(Context ctxt) {
            this.context = ctxt;
            activity = (Activity) ctxt;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            type = params[0];

            String login_url = "http://10.42.0.1/EasyPetrol/login.php";

            if (type.equals("login")) {
                try {
                    email = params[1];
                    password = params[2];

                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                    Log.d("Background", "Post Data = " + post_data);
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream stream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));
                    String result = "Success ";
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    stream.close();
                    httpURLConnection.disconnect();
                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (type.equals("login")) {
                System.out.println("Background :: result = " + result);
                try {
                    JSONObject jsonObjet = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
                    JSONArray jsonArray = jsonObjet.getJSONArray("server_response");
                    JSONObject jo = jsonArray.getJSONObject(0);
                    String code = jo.getString("code");
                    String message = jo.getString("message");
                    if (code.equals("login_true")) {
                        new GetContacts().execute();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        Toast.makeText(activity, "" + message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                }
            }
        }
    }


        private class GetContacts extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {

                HttpHandler sh = new HttpHandler();

                String jsonStr = sh.makeServiceCall("http://10.42.0.1/EasyPetrol/user_api.php?Email=" + email);
                return jsonStr;
            }

            @Override
            protected void onPostExecute(String jsonStr) {
                super.onPostExecute(jsonStr);

                if (jsonStr != null) {
                    try {

                        JSONArray jsonArray = new JSONArray(jsonStr);

                        // looping through All Contacts
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            String Email = c.getString("Email");
                            String Image = c.getString("Image");
                            String Phone = c.getString("Phone");
                            String Address = c.optString("Address");
                            String Longitude = c.optString("Longitude");
                            String Latitude = c.optString("Latitude");
//                            double lat = Double.parseDouble(Latitude);
//                            double lng = Double.valueOf(Longitude);
                            String type = c.optString("type");
                            String State = c.getString("State");
                            userDb.insertData(Email, password, password, Image, Phone, Address, 0.0, 0.0, type, State);
                            System.out.println("STate :" + State);
                        }
                    } catch (final JSONException e) {


                    }
                }
            }

        }


}