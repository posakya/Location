package com.example.roshan.easypetrol.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.roshan.easypetrol.DbHandler.User_Profile;
import com.example.roshan.easypetrol.HttpHandler;
import com.example.roshan.easypetrol.MainActivity;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static android.R.attr.bitmap;

/**
 * Created by roshan on 7/3/17.
 */

public class Background extends AsyncTask<String, Void, String> {
    Context context;
    Activity activity;
    String type;
    String email;
    String password ;
    String confirm;
    String image ;
    String telephone;
    String address ;
    String logitude ;
    String latitude;
    String type1;
    String state;
    Bitmap bitmap;
    private User_Profile dbUser;

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
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String register_url="http://10.42.0.1/EasyPetrol/register.php";
       // String login_url="http://10.42.0.1/EasyPetrol/login.php";
        if (type.equals("register")) {
            try {
                email = params[1];
                password = params[2];
                confirm = params[3];
                image = params[4];
                telephone = params[5];
                address = params[6];
                logitude =params[7];
                latitude=params[8];
                type1=params[9];
                state=params[10];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +  URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" + URLEncoder.encode("Confirm_pass", "UTF-8") + "=" + URLEncoder.encode(confirm, "UTF-8") + "&" + URLEncoder.encode("Image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") + "&" + URLEncoder.encode("Phone", "UTF-8") + "=" + URLEncoder.encode(telephone, "UTF-8") + "&" + URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" + URLEncoder.encode("Longitude", "UTF-8") + "=" + URLEncoder.encode(logitude, "UTF-8") + "&" + URLEncoder.encode("Latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type1, "UTF-8") + "&" + URLEncoder.encode("State", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
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
        }else if (type.equals("update")) {
            try {
                email = params[1];
                password = params[2];
                confirm = params[3];
                image = params[4];
                telephone = params[5];
                address = params[6];
                logitude =params[7];
                latitude=params[8];
                type1=params[9];
                state=params[10];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +  URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" + URLEncoder.encode("Confirm_pass", "UTF-8") + "=" + URLEncoder.encode(confirm, "UTF-8") + "&" + URLEncoder.encode("Image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8") + "&" + URLEncoder.encode("Phone", "UTF-8") + "=" + URLEncoder.encode(telephone, "UTF-8") + "&" + URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" + URLEncoder.encode("Longitude", "UTF-8") + "=" + URLEncoder.encode(logitude, "UTF-8") + "&" + URLEncoder.encode("Latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8") + "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type1, "UTF-8") + "&" + URLEncoder.encode("State", "UTF-8") + "=" + URLEncoder.encode(state, "UTF-8");
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
        if (type.equals("register")) {
            try {
                JSONObject jsonObjet = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
                String code = jsonObjet.getString("success");

                if (code.equals("1")) {
                    activity.finish();
                    Toast.makeText(activity, "" + code, Toast.LENGTH_SHORT).show();
                    activity.startActivity(new Intent(activity, MainActivity.class));
                }
                if (code.equals("Invalide Email and Password")) {
                    Toast.makeText(activity, "" + code, Toast.LENGTH_SHORT).show();
                }
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }if (type.equals("update")){
            Toast.makeText(activity, "Data Updated", Toast.LENGTH_SHORT).show();
        }
        }
}
