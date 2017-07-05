package com.example.roshan.easypetrol.DbHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by roshan on 5/25/17.
 */

public class User_Profile extends SQLiteOpenHelper {

    public static final String database_name="easy_petrol";

    //create table
    public static final String table_name="user_profile";
    public static final String col_1=" _id";
    public static final String col_2="email";
    public static final String col_3="password";
    public static final String col_4="re_password";
    public static final String col_5="image";
    public static final String col_6="phone";
    public static final String col_7="address";
    public static final String col_8="lng";
    public static final String col_9="lat";
    public static final String col_10="user_type";
    public static final String col_11="state";


    public User_Profile(Context context) {
        super(context, database_name, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + table_name + "( _id integer primary key autoincrement ,email text,password text,re_password text, image text,phone integer,address text,lng double,lat double,user_type text,state integer)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + table_name);
        // db.execSQL("ALTER TABLE "+ table_name +" ADD "+ col_1 +" INTEGER PRIMARY KEY AUTOINCREMENT");
        onCreate(db);
    }

    public void insertData(String email, String password, String re_password, String image, String phone, String address, double lng, double lat,String user_type,String state){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(col_2,email);
        values.put(col_3,password);
        values.put(col_4, re_password);
        values.put(col_5, image);
        values.put(col_6,phone);
        values.put(col_7, address);
        values.put(col_8, lng);
        values.put(col_9, lat);
        values.put(col_10,user_type);
        values.put(col_11,state);
        int u = db.update("user_profile", values, "email=?", new String[]{email});
        if (u == 0) {
            db.insertWithOnConflict("user_profile", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public Cursor getUserData(){
        SQLiteDatabase db=this.getWritableDatabase();

        Cursor cr =  db.rawQuery( "select _id as _id,  email, password, re_password, image, phone, address, lng, lat, user_type, state from user_profile",null);

        if (cr != null) {
            cr.moveToFirst();
        }
        Log.d("Cursor Size ", cr.getCount() +"");
        cr.getCount();
        return cr;
    }

    public void deleteData(){
        SQLiteDatabase db=this.getWritableDatabase();
        String delete=("delete from user_profile");
        db.execSQL(delete);
    }
}
