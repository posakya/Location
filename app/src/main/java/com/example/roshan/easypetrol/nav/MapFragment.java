package com.example.roshan.easypetrol.nav;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.roshan.easypetrol.Authentication.Background;
import com.example.roshan.easypetrol.Authentication.RegisterActivity;
import com.example.roshan.easypetrol.DbHandler.User_Profile;
import com.example.roshan.easypetrol.HttpHandler;
import com.example.roshan.easypetrol.MainActivity;
import com.example.roshan.easypetrol.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.maps.android.SphericalUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
private View view;

    private MapView mMapView;
    private GoogleMap mMap;
    private User_Profile dbUSer;
    Marker marker;
    boolean isGPSEnabled = false;
    //From -> the first coordinate from where we need to calculate the distance
    private String lng;
    private String lat;
    String email;
    String password;
    String re_password;
    String image;
    String address;
    String phone;
    String user_type;
    String State;
    String Status;
    double latitude;
    double longitude;
    private ImageView imageview;
    private Button btn_share,btn_on_state,btn_off_State;
//    private static String url = "http://realthree60.com/dev/apis/AgentProfile/1";
    ArrayList<HashMap<String, String>> absentlist;
    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_map, container, false);
        dbUSer=new User_Profile(getActivity());
//        if (isGPSEnabled){
//            showSettingsAlert();
//        }
        CheckEnableGPS();

        mMapView = (MapView) view.findViewById(R.id.map);
        btn_share=(Button)view.findViewById(R.id.btn_share);
        btn_on_state=(Button)view.findViewById(R.id.btn_on_state);
        btn_off_State=(Button)view.findViewById(R.id.btn_off_state);
        imageview=(ImageView)view.findViewById(R.id.imageView);
     //   setHasOptionsMenu(true);
      //  actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        btn_off_State.setVisibility(View.GONE);
        btn_on_state.setVisibility(View.GONE);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

        return view;
    }

    public boolean checkInternetOn() {

        ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnectedOrConnecting()) {
                // Internet Available

            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
        return netInfo!=null && netInfo.isConnected();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
              LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                latitude=location.getLatitude();
                longitude=location.getLongitude();

                System.out.println("lat :"+latitude);
                System.out.println("lon :"+longitude);

                if (marker != null) {
                    marker.remove();
                }
                marker = mMap.addMarker(new MarkerOptions()
                        .title(String.valueOf(loc)).snippet("Jadibuti").position(loc));

                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(loc)
                        .zoom(13)
                        .bearing(90)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        2000, null);
//                mMap.addMarker(new MarkerOptions().position(loc)
//                        .title(String.valueOf(loc)));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                Cursor cursor=dbUSer.getUserData();
                email=cursor.getString(cursor.getColumnIndex(dbUSer.col_2));
                password=cursor.getString(cursor.getColumnIndex(dbUSer.col_3));
                re_password=cursor.getString(cursor.getColumnIndex(dbUSer.col_4));
                image=cursor.getString(cursor.getColumnIndex(dbUSer.col_5));
                System.out.println("image :"+image);
                phone=cursor.getString(cursor.getColumnIndex(dbUSer.col_6));
                address=cursor.getString(cursor.getColumnIndex(dbUSer.col_7));
                lng=String.valueOf(longitude);
                lat=String.valueOf(latitude);
                System.out.println("Latitude :"+lat);
                System.out.println("Longitude :"+lng);
                user_type=cursor.getString(cursor.getColumnIndex(dbUSer.col_10));
                Status=cursor.getString(cursor.getColumnIndex(dbUSer.col_11));

                Picasso.with(getActivity()).load(image).into(imageview);

                if (Status.equals("1")){
                    btn_off_State.setVisibility(View.GONE);
                    btn_on_state.setVisibility(View.VISIBLE);
                }
                if (Status.equals("0")){
                    btn_off_State.setVisibility(View.VISIBLE);
                    btn_on_state.setVisibility(View.GONE);
                }
                if (checkInternetOn()) {
                    new GetContacts().execute();
                    new marker().execute();
                }
                else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

//

                btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //    dbUSer.insertData(email,password,re_password,image,phone,address,longitude,latitude,user_type,state);
                        if (checkInternetOn()) {
                            new Background(getActivity()).execute("update", email, password, re_password, image, phone, address, lng, lat, user_type, "1");
                            new GetContacts().execute();
                            dbUSer.insertData(email,password,re_password,image,phone,address,longitude,latitude,user_type,"1");
                            Toast.makeText(getActivity(), "Location shared"+lat, Toast.LENGTH_SHORT).show();
                        }  else {
                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btn_on_state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkInternetOn()) {
                            new Background(getActivity()).execute("update", email, password, re_password, image, phone, address, lng, lat, user_type, "0");
                            new GetContacts().execute();
                            dbUSer.insertData(email,password,re_password,image,phone,address,longitude,latitude,user_type,"0");
                            Toast.makeText(getActivity(), "State "+State, Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                btn_off_State.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkInternetOn()) {
                            new Background(getActivity()).execute("update", email, password, re_password, image, phone, address, lng, lat, user_type, "1");
                            new GetContacts().execute();
                            dbUSer.insertData(email,password,re_password,image,phone,address,longitude,latitude,user_type,"1");
                            Toast.makeText(getActivity(), "State "+State, Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        };

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);

    }

//    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
//        StringBuilder urlString = new StringBuilder();
//        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
//        urlString.append("?origin=");// from
//        urlString.append(Double.toString(sourcelat));
//        urlString.append(",");
//        urlString
//                .append(Double.toString( sourcelog));
//        urlString.append("&destination=");// to
//        urlString
//                .append(Double.toString( destlat));
//        urlString.append(",");
//        urlString.append(Double.toString(destlog));
//        urlString.append("&sensor=false&mode=driving&alternatives=true");
//        urlString.append("&key=SERVER-KEY");
//        return urlString.toString();
//    }
//
//    //The parameter is the server response
//    public void drawPath(String  result) {
//        //Getting both the coordinates
//        LatLng from = new LatLng(fromLatitude,fromLongitude);
//        LatLng to = new LatLng(toLatitude,toLongitude);
//
//        //Calculating the distance in meters
//        Double distance = SphericalUtil.computeDistanceBetween(from, to);
//
//        //Displaying the distance
//        Toast.makeText(getActivity(),String.valueOf(distance+" Meters"),Toast.LENGTH_SHORT).show();
//
//
//        try {
//            //Parsing json
//            final JSONObject json = new JSONObject(result);
//            JSONArray routeArray = json.getJSONArray("routes");
//            JSONObject routes = routeArray.getJSONObject(0);
//            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
//            String encodedString = overviewPolylines.getString("points");
//            List<LatLng> list = decodePoly(encodedString);
//            Polyline line = mMap.addPolyline(new PolylineOptions()
//                    .addAll(list)
//                    .width(20)
//                    .color(Color.RED)
//                    .geodesic(true)
//            );
//
//
//        }
//        catch (JSONException e) {
//
//        }
//    }
//
//    private List<LatLng> decodePoly(String encoded) {
//        List<LatLng> poly = new ArrayList<LatLng>();
//        int index = 0, len = encoded.length();
//        int lat = 0, lng = 0;
//
//        while (index < len) {
//            int b, shift = 0, result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            LatLng p = new LatLng( (((double) lat / 1E5)),
//                    (((double) lng / 1E5) ));
//            poly.add(p);
//        }
//
//        return poly;
//    }
  //  [(23,)]

//    public void showSettingsAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                getActivity().startActivity(intent);
//            }
//        });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }
private void CheckEnableGPS(){
    String provider = Settings.Secure.getString(getActivity().getContentResolver(),
            Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
    if(!provider.equals("")){
        //GPS Enabled
        Toast.makeText(getActivity(), "GPS Enabled: " + provider,
                Toast.LENGTH_LONG).show();
    }else{
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

}

//    public void onStart(){
//        super.onStart();
//
//    }


    private class GetContacts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog=new ProgressDialog(getContext());
//            pDialog.setMessage("Please Wait..");
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall("http://10.42.0.1/EasyPetrol/api.php?Email="+email);
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray jsonArray=new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                         String Email = c.getString("Email");
                         State = c.getString("State");

                         System.out.println("STate :"+State);

                        if (State.equals("1")){
                            btn_on_state.setVisibility(View.VISIBLE);
                            btn_off_State.setVisibility(View.GONE);
                        }

                        if (State.equals("0")){
                            btn_off_State.setVisibility(View.VISIBLE);
                            btn_on_state.setVisibility(View.GONE);
                        }
                    }
                } catch (final JSONException e) {


                }
            }
        }

    }

    private class marker extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog=new ProgressDialog(getContext());
//            pDialog.setMessage("Please Wait..");
//            pDialog.setCancelable(false);
//            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall("http://10.42.0.1/EasyPetrol/pump_api.php");
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String jsonStr) {
            super.onPostExecute(jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray jsonArray=new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        String Id=c.optString("Id");
                        String Email = c.getString("Email");
                        final String Phone =c.optString("Phone");
                        String Address=c.optString("Address");
                        String Longitude =c.optString("Longitude");
                        String Latitude =c.optString("Latitude");
                        double lat=Double.parseDouble(Latitude);
                        double lng=Double.parseDouble(Longitude);
                        LatLng location=new LatLng(lng,lat);

                        marker = mMap.addMarker(new MarkerOptions()
                                .title(String.valueOf(location)).snippet(Phone).position(location));

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                            @Override
                            public void onInfoWindowClick(Marker arg0) {
                                // TODO Auto-generated method stub

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + Phone));
                                startActivity(callIntent);

                                Toast.makeText(getActivity(), "" + Phone, Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                } catch (final JSONException e) {


                }
            }
        }

    }

}
