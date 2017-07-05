package com.example.roshan.easypetrol.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roshan.easypetrol.DbHandler.User_Profile;
import com.example.roshan.easypetrol.HttpHandler;
import com.example.roshan.easypetrol.MainActivity;
import com.example.roshan.easypetrol.R;
import com.example.roshan.easypetrol.WebServices;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.bitmap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edit_email,edit_pass,edit_city,edit_address,edit_phone,edit_confirm_pass,edit_Fname,edit_lname;
    private Button btn_register;
    private TextView txt_sign_in;
    String img_id;
    String email;
    String password;
    String phone;
    String confirm_pass;
    String address;
    private User_Profile dbUser;
    double lng;
    double lat;
    private RadioGroup rbg;
    private RadioButton rb_user,rb_pump;
    private ImageView imageView;
    String user_type;
    Bitmap bm1;
    public static final int MEDIA_TYPE_IMAGE =1;
    final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    final int RQS_IMAGE1 = 1;
    private String imagepath=null;
    int c;
    String picturePath;
    boolean fromCam = false;
    Uri source1;
    String  state="1";
    public static final String register_url="http://10.42.0.1/EasyPetrol/register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbUser=new User_Profile(getApplicationContext());
        rb_pump=(RadioButton)findViewById(R.id.rb_pump);
        rb_user=(RadioButton)findViewById(R.id.rb_user);
        edit_email=(EditText)findViewById(R.id.edit_email);
        edit_pass=(EditText)findViewById(R.id.edit_password);
        edit_address=(EditText)findViewById(R.id.edit_address);
        edit_confirm_pass=(EditText)findViewById(R.id.edit_confirm_password);
        edit_phone=(EditText)findViewById(R.id.edit_phone);
        btn_register=(Button)findViewById(R.id.btn_register);
        txt_sign_in=(TextView)findViewById(R.id.txt_sign_in);
        imageView=(ImageView)findViewById(R.id.user_image);

        Cursor cursor=dbUser.getUserData();
        if (cursor.getCount()==1){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });
        btn_register.setOnClickListener(this);
        txt_sign_in.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View v) {
        if (v==btn_register){
            register();
            Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
            animation1.setDuration(1000);
            v.startAnimation(animation1);
        }
        if (v==txt_sign_in){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source1 = data.getData();
                    try {
                        System.out.println("Bitmap path = "+source1.getPath());
                         bm1 = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source1));
                        imageView.setImageBitmap(bm1);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    private void register() {
        if (rb_pump.isChecked()){
            user_type="pump";
        }
        if (rb_user.isChecked()){
            user_type="user";
        }
        email=edit_email.getText().toString();
        password=edit_pass.getText().toString();
        address=edit_address.getText().toString();
        phone=edit_phone.getText().toString();
        confirm_pass=edit_confirm_pass.getText().toString();


        String image = getStringImage(bm1);
        System.out.println("Image :"+image);


        if (email.equals("")||password.equals("")||address.equals("")||password.equals("")||confirm_pass.equals("")){
            Toast.makeText(RegisterActivity.this, "Some field must be filled", Toast.LENGTH_LONG).show();

            return;
        }  if(password.length() <= 5 || confirm_pass.length() <= 5){

            Toast.makeText(RegisterActivity.this, "password length must be greater than one", Toast.LENGTH_LONG).show();

            return;

        }
        if (confirm_pass.equals(password)){

         new Background1(RegisterActivity.this).execute("register",email,password,confirm_pass,image,phone,address,"0","0",user_type,"1");
           dbUser.insertData(email,password,confirm_pass,picturePath,phone,address,lng,lat,user_type,"1");
            System.out.println("Inserting :");
            return;
        }else {
            Toast.makeText(this, "Password and Confirm Password doesn't match", Toast.LENGTH_SHORT).show();
        }
    }

    //json Parse


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

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public class Background1 extends AsyncTask<String, Void, String> {
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

        public Background1(Context ctxt) {
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

            String fileName = source1.getPath();
            int maxBufferSize = 1 * 1024 * 1024;
//            File sourceFile = new File(imagepath);
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
                  //  FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(register_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    httpURLConnection.setRequestProperty("Image", fileName);
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
            }
        }
    }



}
